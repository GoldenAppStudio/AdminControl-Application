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

public class SubServiceAdd extends AppCompatActivity {

    private EditText editText;
    private CircleImageView circleImageView;
    private Button button;
    public static Uri mainImageURI = null;
    private boolean isChanged = false;
    private Bitmap compressedImageFile;
    private StorageReference storageReference;
    int count;
    String abc;
    int countX;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_service_add);

        getSupportActionBar().setTitle("Register New Sub-Service");

        editText = findViewById(R.id.xSServiceName);
        circleImageView = findViewById(R.id.xSServiceImage);
        button = findViewById(R.id.xSSubmit);
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             countX = extras.getInt("key");
             abc = extras.getString("name");
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("ServiceList/" + countX + "/subService/");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(v -> {

            if (editText.getText().toString().isEmpty() || mainImageURI == null) {
                Toast.makeText(SubServiceAdd.this, "Please insert name and/or logo", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog = new ProgressDialog(SubServiceAdd.this);
            progressDialog.setMessage("Adding sub-service to database...");
            progressDialog.show();

            File newImageFile = new File(mainImageURI.getPath());
            try {

                compressedImageFile = new Compressor(SubServiceAdd.this)
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

            UploadTask image_path = storageReference.child("service").child("sub-service").child(abc).child(editText.getText().toString() + ".jpg").putBytes(thumbData);

            image_path.addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    ref.child(String.valueOf(count + 1)).child("name").setValue("" + editText.getText().toString());
                    progressDialog.dismiss();
                    Toast.makeText(this, "Service is added to database", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SubServiceAdd.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(SubServiceAdd.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                }
            });
        });

        circleImageView.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(SubServiceAdd.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(SubServiceAdd.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(SubServiceAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                .start(SubServiceAdd.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                circleImageView.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}
