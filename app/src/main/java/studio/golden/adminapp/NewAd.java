package studio.golden.adminapp;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class NewAd extends AppCompatActivity {

    EditText AdName, AdEmail, AdNo, AdAdress, AdSD, AdLD, web, P;
    Button AdOK;
    CircleImageView AdImage;
    public static Uri mainImageURI = null;
    private boolean isChanged = false;
    private Bitmap compressedImageFile;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);

        storageReference = FirebaseStorage.getInstance().getReference();

        AdName = findViewById(R.id.AdName); AdEmail = findViewById(R.id.AdEmail);
        AdAdress = findViewById(R.id.AdAddress); AdNo = findViewById(R.id.AdNo);
        AdSD = findViewById(R.id.AdSD); AdLD = findViewById(R.id.AdLD);
        AdOK = findViewById(R.id.AdOK); AdImage = findViewById(R.id.AdImage);
        web = findViewById(R.id.AdWeb);
        P = findViewById(R.id.Priority);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("ShowAd/");
        final long[] count = new long[1];

        AdOK.setOnClickListener(v -> {

            if(AdName.getText().toString().isEmpty() || AdSD.getText().toString().isEmpty() || AdLD.getText().toString().isEmpty() || mainImageURI == null){
                Toast.makeText(this, "Please fill all with mark *", Toast.LENGTH_SHORT).show();
                return;
            }

            if(P.getText().toString().isEmpty()){
                Toast.makeText(this, "Please fill up the priority", Toast.LENGTH_SHORT).show();
                return;
            }


            progressDialog = new ProgressDialog(NewAd.this);
            progressDialog.setMessage("Preparing In-App Ad...");
            progressDialog.show();

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    count[0] = dataSnapshot.getChildrenCount();

                    File newImageFile = new File(mainImageURI.getPath());
                    try {

                        compressedImageFile = new Compressor(NewAd.this)
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

                    UploadTask image_path = storageReference.child("ads_images").child(String.valueOf(count[0] +1) + ".jpg").putBytes(thumbData);

                    image_path.addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            ref.child(String.valueOf(count[0] +1)).child("name").setValue(AdName.getText().toString());
                            ref.child(String.valueOf(count[0] +1)).child("id").setValue((int) (count[0] + 1));
                            ref.child(String.valueOf(count[0] +1)).child("website").setValue(web.getText().toString());
                            ref.child(String.valueOf(count[0] +1)).child("address").setValue(AdAdress.getText().toString());
                            ref.child(String.valueOf(count[0] +1)).child("longDisc").setValue(AdLD.getText().toString());
                            ref.child(String.valueOf(count[0] +1)).child("shortDisc").setValue(AdSD.getText().toString());
                            ref.child(String.valueOf(count[0] +1)).child("phone").setValue(AdNo.getText().toString());
                            ref.child(String.valueOf(count[0] +1)).child("email").setValue(AdEmail.getText().toString());
                            ref.child(String.valueOf(count[0] +1)).child("priority").setValue(Long.valueOf(P.getText().toString()));
                            progressDialog.dismiss();
                            Toast.makeText(NewAd.this, "In-App Ad is scheduled", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(NewAd.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(NewAd.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();
                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });


        AdImage.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(NewAd.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(NewAd.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(NewAd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                .start(NewAd.this);
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
