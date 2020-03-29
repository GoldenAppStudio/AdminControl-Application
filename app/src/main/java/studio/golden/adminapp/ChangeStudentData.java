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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ChangeStudentData extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner;
    int count;
    String service;
    private EditText editText;
    TextView ab, ac;
    private CircleImageView circleImageView;
    public static Uri mainImageURI = null;
    private boolean isChanged = false;
    private Bitmap compressedImageFile;
    private StorageReference storageReference;
    Button zSubmit, zDelete, zSS;
    int totalCount;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_student_data);

        getSupportActionBar().setTitle("Edit Service");
        spinner = findViewById(R.id.ppq);
        circleImageView = findViewById(R.id.aa);
        ab = findViewById(R.id.ab);
        ac = findViewById(R.id.ac);
        zSS = findViewById(R.id.zSS);
        Button az = findViewById(R.id.az);
        editText = findViewById(R.id.ad);
        zDelete = findViewById(R.id.zDelete);
        zSubmit = findViewById(R.id.zSubmit);
        AtomicReference<ProgressDialog> progressDialog = new AtomicReference<>(new ProgressDialog(ChangeStudentData.this));
        progressDialog.get().setMessage("Loading please wait...");
        progressDialog.get().show();
        storageReference = FirebaseStorage.getInstance().getReference();

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("ServiceList/");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ServiceList/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    totalCount = (int) snapshot.getChildrenCount();
                    if(dataSnapshot.child("service").getValue().toString().equals("Travel")){
                        continue;
                    }
                    categories.add("" + dataSnapshot.child("service").getValue().toString());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ChangeStudentData.this, android.R.layout.simple_spinner_item, categories);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    progressDialog.get().dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        az.setOnClickListener(v -> {
            progressDialog.get().setMessage("Just a moment...");
            progressDialog.get().show();
            spinner.setVisibility(View.GONE);
            ab.setVisibility(View.GONE);
            circleImageView.setVisibility(View.VISIBLE);
            ac.setVisibility(View.VISIBLE);
            zDelete.setVisibility(View.VISIBLE);
            zSS.setVisibility(View.VISIBLE);
            zSubmit.setVisibility(View.VISIBLE);
            az.setVisibility(View.GONE);

             databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     StorageReference gsReference = storage.getReferenceFromUrl("gs://serviceapp-67984.appspot.com/service/" + service + ".jpg");
                     gsReference.getDownloadUrl().addOnSuccessListener(uri -> {
                         Glide.with(ChangeStudentData.this).load(uri.toString()).into(circleImageView);
                       //  mainImageURI = uri;
                     }).addOnFailureListener(exception -> {
                         // Handle any errors
                     });
                     progressDialog.get().dismiss();
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
        });

        zSS.setOnClickListener(v -> {
            Intent intent = new Intent(ChangeStudentData.this, SubServiceAdd.class);
            intent.putExtra("key", count);
            intent.putExtra("name", service);
            startActivity(intent);
        });

        zSubmit.setOnClickListener(v -> {

            if (mainImageURI == null) {

                    Toast.makeText(this, "Please re-upload logo since service key is changed. You can re-upload the same logo", Toast.LENGTH_LONG).show();
                } else {


                    progressDialog.set(new ProgressDialog(ChangeStudentData.this));
                    progressDialog.get().setMessage("Updating service to database...");
                    progressDialog.get().show();

                    File newImageFile = new File(mainImageURI.getPath());
                    try {

                        compressedImageFile = new Compressor(ChangeStudentData.this)
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

                    StorageReference storageRef = storage.getReference();
                    StorageReference desertRef = storageRef.child("service/" + service + ".jpg");

                    desertRef.delete().addOnSuccessListener(aVoid -> {
                        // File deleted successfully
                    }).addOnFailureListener(exception -> {
                        // Uh-oh, an error occurred!
                    });

                    UploadTask image_path = storageReference.child("service").child(service + ".jpg").putBytes(thumbData);

                    image_path.addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {


                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(ChangeStudentData.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                        }
                    });

                    progressDialog.get().dismiss();
                    Toast.makeText(this, "Service added to database", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChangeStudentData.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            });

        zDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("If you remove service then its provider will also be removed. It can not be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Proceed", (dialog, which) -> {
                        ChangeStudentData.super.onBackPressed();
                        progressDialog.set(new ProgressDialog(ChangeStudentData.this));
                        progressDialog.get().setMessage("Removing from database...");
                        progressDialog.get().show();
                        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference("ServiceList/")
                                .child(String.valueOf(count));
                        mPostReference.removeValue();

                        StorageReference storageRef = storage.getReference();
                        StorageReference desertRef = storageRef.child("service/" + service + ".jpg");

                        desertRef.delete().addOnSuccessListener(a -> {
                        }).addOnFailureListener(exception -> {
                        });
                        if (totalCount > count){
                            for(int i = 0; i < totalCount - count; i++){
                                int finalI = i;

                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ref.child(String.valueOf(count + finalI)).setValue(dataSnapshot.child(String.valueOf(count + finalI + 1)).getValue());

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            DatabaseReference PostReference = FirebaseDatabase.getInstance().getReference("ServiceList/")
                                    .child(String.valueOf(totalCount));
                            PostReference.removeValue();

                        }

                        progressDialog.get().dismiss();
                        Toast.makeText(this, "Service removed from database", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangeStudentData.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }).create().show();
        });

        circleImageView.setOnClickListener(v -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(ChangeStudentData.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(ChangeStudentData.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(ChangeStudentData.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    BringImagePicker();
                }
            } else {
                BringImagePicker();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        service = spinner.getSelectedItem().toString();
        count = position + 2;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ChangeStudentData.this);
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