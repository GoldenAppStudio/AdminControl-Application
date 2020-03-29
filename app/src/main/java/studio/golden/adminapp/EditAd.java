/***************************************************
            Coded by GoldenApp Studio
***************************************************/



package studio.golden.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class EditAd extends AppCompatActivity {

    EditText AdName, AdEmail, AdNo, AdAdress, AdSD, AdLD, web, p;
    Button AdOK;
    CircleImageView AdImage;
    public static Uri mainImageURI = null;
    private boolean isChanged = false;
    private Bitmap compressedImageFile;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        progressDialog = new ProgressDialog(EditAd.this);
        progressDialog.setMessage("Preparing In-App Ad...");
        progressDialog.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uid = extras.getInt("uid");
        }
        storageReference = FirebaseStorage.getInstance().getReference();

        AdName = findViewById(R.id.AdNameX); AdEmail = findViewById(R.id.AdEmailX);
        AdAdress = findViewById(R.id.AdAddressX); AdNo = findViewById(R.id.AdNoX);
        AdSD = findViewById(R.id.AdSDX); AdLD = findViewById(R.id.AdLDX);
        AdOK = findViewById(R.id.AdOKX); AdImage = findViewById(R.id.AdImageX);
        web = findViewById(R.id.AdWebX); p = findViewById(R.id.pAD);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("ShowAd/" + String.valueOf(uid));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    progressDialog.dismiss();
                    AdName.setEnabled(false);
                    AdImage.setEnabled(false);
                    AdAdress.setEnabled(false);
                    AdEmail.setEnabled(false);
                    AdNo.setEnabled(false);
                    AdSD.setEnabled(false);
                    AdLD.setEnabled(false);
                    web.setEnabled(false);
                    AdOK.setEnabled(false);
                    new AlertDialog.Builder(EditAd.this)
                            .setTitle("Create Ad")
                            .setMessage("Please create add before editing.")
                            .setPositiveButton("Proceed", (dialog, which) -> {
                                Intent intent = new Intent(EditAd.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }).create().show();

                } else {
                    AdName.setText(snapshot.child("name").getValue().toString());
                    AdAdress.setText(snapshot.child("address").getValue().toString());
                    AdEmail.setText(snapshot.child("email").getValue().toString());
                    AdNo.setText(snapshot.child("phone").getValue().toString());
                    AdLD.setText(snapshot.child("longDisc").getValue().toString());
                    AdSD.setText(snapshot.child("shortDisc").getValue().toString());
                    web.setText(snapshot.child("website").getValue().toString());
                    p.setText(snapshot.child("priority").getValue().toString());

                    StorageReference gsReference = storage.getReferenceFromUrl("gs://serviceapp-67984.appspot.com/profile_images/Plumber.jpg");
                    gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(EditAd.this).load(uri.toString()).into(AdImage);
                        //  mainImageURI = uri;
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                    });

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        AdOK.setOnClickListener(v -> {

            if(AdName.getText().toString().isEmpty() || AdSD.getText().toString().isEmpty() || AdLD.getText().toString().isEmpty() || p.getText().toString().isEmpty()){
                Toast.makeText(this, "Please fill all with mark *", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mainImageURI == null) {
                ref.child("name").setValue(AdName.getText().toString());
                ref.child("website").setValue(web.getText().toString());
                ref.child("address").setValue(AdAdress.getText().toString());
                ref.child("longDisc").setValue(AdLD.getText().toString());
                ref.child("shortDisc").setValue(AdSD.getText().toString());
                ref.child("phone").setValue(AdNo.getText().toString());
                ref.child("email").setValue(AdEmail.getText().toString());
                ref.child("priority").setValue(Long.valueOf(p.getText().toString()));
                progressDialog.dismiss();
                Toast.makeText(this, "In-App Ad is updated and scheduled", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditAd.this, MainActivity.class);
                startActivity(intent);
            } else {

                File newImageFile = new File(mainImageURI.getPath());
                try {

                    compressedImageFile = new Compressor(EditAd.this)
                            .setMaxHeight(125)
                            .setMaxWidth(125)
                            .setQuality(50)
                            .compressToBitmap(newImageFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbData = baos.toByteArray();

                UploadTask image_path = storageReference.child("profile_images").child(uid + ".jpg").putBytes(thumbData);

                image_path.addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        ref.child("name").setValue(AdName.getText().toString());
                        ref.child("website").setValue(web.getText().toString());
                        ref.child("address").setValue(AdAdress.getText().toString());
                        ref.child("longDisc").setValue(AdLD.getText().toString());
                        ref.child("shortDisc").setValue(AdSD.getText().toString());
                        ref.child("phone").setValue(AdNo.getText().toString());
                        ref.child("email").setValue(AdEmail.getText().toString());
                        ref.child("priority").setValue(Long.valueOf(p.getText().toString()));
                        progressDialog.dismiss();
                        Toast.makeText(this, "In-App Ad is updated and scheduled", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditAd.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(EditAd.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                    }
                });

            }
        });


        AdImage.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(EditAd.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(EditAd.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(EditAd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    BringImagePicker();
                }
            } else {
                BringImagePicker();
            }
        });

    }


    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditAd.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                AdImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
