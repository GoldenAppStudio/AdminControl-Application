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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSP extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText editTextTitle;
    private EditText editTextPrice;
    private EditText editTextEmail;
    private EditText editTextPhone;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    private EditText editTextAddress;
    private EditText editTextDescription;
    private Button submit, bbbb;
    int count;
    String email, price, spname, description, service, phone, state, address , sub;

    private Spinner spinner, mState, mDistrict, ssss;
    TextView tttt;

    private CircleImageView setupImage;
    public static Uri mainImageURI = null;

    private boolean isChanged = false;

    private StorageReference storageReference;

    private Bitmap compressedImageFile;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sp);

        final String[] district = new String[1];

        storageReference = FirebaseStorage.getInstance().getReference();

        setupImage = findViewById(R.id.setup_image);

        spinner = findViewById(R.id.class_name);
        mState = findViewById(R.id.state_spinner);
        mDistrict = findViewById(R.id.district_chooser);
        ssss = findViewById(R.id.ssss);

        progressDialog = new ProgressDialog(AddSP.this, R.style.MyTheme);
        progressDialog.setMessage("Loading please wait...");
        progressDialog.show();
        tttt = findViewById(R.id.tttt);

        mState.setOnItemSelectedListener(this);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        ssss.setOnItemSelectedListener(this);
        List<String> c = new ArrayList<String>();


        databaseReference = FirebaseDatabase.getInstance().getReference("ServiceList/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.child("service").getValue().toString().equals("Travel")){
                        continue;
                    }
                    categories.add("" + dataSnapshot.child("service").getValue().toString());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddSP.this, android.R.layout.simple_spinner_item, categories);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();

            }
        });


        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        submit = findViewById(R.id.submitDetails);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextAddress = findViewById(R.id.edit_text_address);
        bbbb = findViewById(R.id.bbbb);

        submit.setOnClickListener(v -> {

            progressDialog = new ProgressDialog(AddSP.this, R.style.MyTheme);
            progressDialog.setMessage("Loading please wait...");
            progressDialog.show();

            spname = editTextTitle.getText().toString();
            description = editTextDescription.getText().toString();
            price = editTextPrice.getText().toString();
            email = editTextEmail.getText().toString();
            phone = editTextPhone.getText().toString();
            service = spinner.getSelectedItem().toString();
            address = editTextAddress.getText().toString();
            editTextAddress.setVisibility(View.GONE);
            editTextTitle.setVisibility(View.GONE);
            editTextPhone.setVisibility(View.GONE);
            editTextEmail.setVisibility(View.GONE);
            editTextPrice.setVisibility(View.GONE);
            editTextDescription.setVisibility(View.GONE);
            mState.setVisibility(View.GONE);
            mDistrict.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            setupImage.setVisibility(View.GONE);
            editTextAddress.setVisibility(View.GONE);
            submit.setVisibility(View.INVISIBLE);
            bbbb.setVisibility(View.VISIBLE);
            ssss.setVisibility(View.VISIBLE);
            tttt.setText("Choose Sub-Service");

            db = FirebaseDatabase.getInstance().getReference("ServiceList/" + String.valueOf(spinner.getSelectedItemPosition() + 2) + "/subService" );

            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        c.add("" + dataSnapshot.child("name").getValue().toString());
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddSP.this, android.R.layout.simple_spinner_item, c);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ssss.setAdapter(dataAdapter);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    progressDialog.dismiss();

                }
            });

            district[0] = mDistrict.getSelectedItem().toString();
            Toast.makeText(AddSP.this, "" + district[0], Toast.LENGTH_SHORT).show();


        });

        bbbb.setOnClickListener(v -> {

            sub = ssss.getSelectedItem().toString();
            if (address.trim().isEmpty() || spname.trim().isEmpty() || description.trim().isEmpty() || phone.trim().isEmpty()) {
                Toast.makeText(AddSP.this, "Please insert all with mark *", Toast.LENGTH_SHORT).show();
                return;
            } else {

                progressDialog = new ProgressDialog(AddSP.this);
                progressDialog.setMessage("Adding to database...");
                progressDialog.show();

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref = database.getReference("Service/"+service+"/"+sub+ "/"+state+"/"+ district[0] +"/");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        count = (int) dataSnapshot.getChildrenCount();
                        Log.d("FETCH", "Count: " + count);
                        if(mainImageURI != null){

                            File newImageFile = new File(mainImageURI.getPath());
                            try {

                                compressedImageFile = new Compressor(AddSP.this)
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
                            Log.d("beforeUpload", "Count: " + count);
                            Toast.makeText(AddSP.this, "" + mDistrict.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                            UploadTask image_path = storageReference.child("service-provider").child(service+sub+ state+ district[0] + String.valueOf(count + 1) + ".jpg").putBytes(thumbData);

                            image_path.addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    Log.d("task", "Count: " + count);
                                    String abc = String.valueOf(count + 1);
                                    ref.child(String.valueOf(count + 1)).child("uid").setValue(abc);
                                    ref.child(String.valueOf(count + 1)).child("priority").setValue(abc);
                                    ref.child(String.valueOf(count + 1)).child("name").setValue("" + spname);
                                    ref.child(String.valueOf(count + 1)).child("phone").setValue("" + phone);
                                    ref.child(String.valueOf(count + 1)).child("email").setValue("" + email);
                                    ref.child(String.valueOf(count + 1)).child("address").setValue("" + address);
                                    ref.child(String.valueOf(count + 1)).child("state").setValue("" + mState.getSelectedItem().toString());
                                    ref.child(String.valueOf(count + 1)).child("district").setValue("" + district[0]);
                                    ref.child(String.valueOf(count + 1)).child("service").setValue("" + service);
                                    ref.child(String.valueOf(count + 1)).child("price").setValue("" + price);
                                    ref.child(String.valueOf(count + 1)).child("description").setValue("" + description);
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(AddSP.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(AddSP.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                }
                            });

                        } else {
                            String abc = String.valueOf(count + 1);
                            ref.child(String.valueOf(count + 1)).child("uid").setValue(abc);
                            ref.child(String.valueOf(count + 1)).child("priority").setValue(abc);
                            ref.child(String.valueOf(count + 1)).child("name").setValue("" + spname);
                            ref.child(String.valueOf(count + 1)).child("phone").setValue("" + phone);
                            ref.child(String.valueOf(count + 1)).child("email").setValue("" + email);
                            ref.child(String.valueOf(count + 1)).child("address").setValue("" + address);
                            ref.child(String.valueOf(count + 1)).child("state").setValue("" + mState.getSelectedItem().toString());
                            ref.child(String.valueOf(count + 1)).child("district").setValue("" + district[0]);
                            ref.child(String.valueOf(count + 1)).child("service").setValue("" + service);
                            ref.child(String.valueOf(count + 1)).child("price").setValue("" + price);
                            ref.child(String.valueOf(count + 1)).child("description").setValue("" + description);
                            progressDialog.dismiss();
                            Intent intent = new Intent(AddSP.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(AddSP.this, "Data added to database", Toast.LENGTH_LONG).show();

            }
        });

        setupImage.setOnClickListener(v -> {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if(ContextCompat.checkSelfPermission(AddSP.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(AddSP.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(AddSP.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

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
                .start(AddSP.this);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sp1= String.valueOf(mState.getSelectedItem());
        if(sp1.contentEquals("Andhra Pradesh")) {
            List<String> list = new ArrayList<String>();
            list.add("Anantapur");
            list.add("Chittoor");
            list.add("East Godavari");
            list.add("Guntur");
            list.add("Krishna");
            list.add("Kurnool");
            list.add("Nellore");
            list.add("Prakasam");
            list.add("Srikakulam");
            list.add("Visakhapatnam");
            list.add("Vizianagaram");
            list.add("West Godavari");
            list.add("YSR Kadapa");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);

        }

        if(sp1.contentEquals("Haryana")) {
            List<String> list = new ArrayList<String>();
            list.add("Ambala");
            list.add("Bhiwani");
            list.add("Charkhi Dadri");
            list.add("Faridabad");
            list.add("Fatehabad");
            list.add("Gurgaon");
            list.add("Hisar");
            list.add("Jhajjar");
            list.add("Jind");
            list.add("Kaithal");
            list.add("Karnal");
            list.add("Kurukshetra");
            list.add("Mahendragarh");
            list.add("Mewat");
            list.add("Palwal");
            list.add("Panchkula");
            list.add("Panipat");
            list.add("Rewari");
            list.add("Rohtak");
            list.add("Sirsa");
            list.add("Sonipat");
            list.add("Yamunanagar");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
          //  Toast.makeText(this, mDistrict.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

        }

        if(sp1.contentEquals("Arunachal Pradesh")) {
            List<String> list = new ArrayList<String>();
            list.add("Tawang");
            list.add("West Kameng");
            list.add("East Kameng");
            list.add("Papum Pare");
            list.add("Kurung Kumey");
            list.add("Kra Daadi");
            list.add("Lower Subansiri");
            list.add("Upper Subansiri");
            list.add("West Siang");
            list.add("East Siang");
            list.add("Siang");
            list.add("Upper Siang");
            list.add("Lower Siang");
            list.add("Lower Dibang Valley");
            list.add("Dibang Valley");
            list.add("Anjaw");
            list.add("Lohit");
            list.add("Namsai");
            list.add("Changlang");
            list.add("Tirap");
            list.add("Longding");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }


        if(sp1.contentEquals("Assam")) {
            List<String> list = new ArrayList<String>();
            list.add("Baksa");
            list.add("Barpeta");
            list.add("Biswanath");
            list.add("Bongaigaon");
            list.add("Cachar");
            list.add("Charaideo");
            list.add("Chirang");
            list.add("Darrang");
            list.add("Dhemaji");
            list.add("Dhubri");
            list.add("Dibrugarh");
            list.add("Goalpara");
            list.add("Golaghat");
            list.add("Hailakandi");
            list.add("Hojai");
            list.add("Jorhat");
            list.add("Kamrup Metropolitan");
            list.add("Kamrup");
            list.add("Karbi Anglong");
            list.add("Karimganj");
            list.add("Kokrajhar");
            list.add("Lakhimpur");
            list.add("Majuli");
            list.add("Morigaon");
            list.add("Nagaon");
            list.add("Nalbari");
            list.add("Dima Hasao");
            list.add("Sivasagar");
            list.add("Sonitpur");
            list.add("South Salmara-Mankachar");
            list.add("Tinsukia");
            list.add("Udalguri");
            list.add("West Karbi Anglong");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);      }


        if(sp1.contentEquals("Bihar")) {
            List<String> list = new ArrayList<String>();
            list.add("Araria");
            list.add("Arwal");
            list.add("Aurangabad");
            list.add("Banka");
            list.add("Begusarai");
            list.add("Bhagalpur");
            list.add("Bhojpur");
            list.add("Buxar");
            list.add("Darbhanga");
            list.add("East Champaran (Motihari)");
            list.add("Gaya");
            list.add("Gopalganj");
            list.add("Jamui");
            list.add("Jehanabad");
            list.add("Kaimur (Bhabua)");
            list.add("Katihar");
            list.add("Khagaria");
            list.add("Kishanganj");
            list.add("Lakhisarai");
            list.add("Madhepura");
            list.add("Madhubani");
            list.add("Munger (Monghyr)");
            list.add("Muzaffarpur");
            list.add("Nalanda");
            list.add("Nawada");
            list.add("Patna");
            list.add("Purnia (Purnea)");
            list.add("Rohtas");
            list.add("Saharsa");
            list.add("Samastipur");
            list.add("Saran");
            list.add("Sheikhpura");
            list.add("Sheohar");
            list.add("Sitamarhi");
            list.add("Siwan");
            list.add("Supaul");
            list.add("Vaishali");
            list.add("West Champaran");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);     }

        if(sp1.contentEquals("Chandigarh (UT)")) {
            List<String> list = new ArrayList<String>();
            list.add("Chandigarh");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Chhattisgarh")) {
            List<String> list = new ArrayList<String>();
            list.add("Balod");
            list.add("Baloda Bazar");
            list.add("Balrampur");
            list.add("Bastar");
            list.add("Bemetara");
            list.add("Bijapur");
            list.add("Bilaspur");
            list.add("Dantewada (South Bastar)");
            list.add("Dhamtari");
            list.add("Durg");
            list.add("Gariyaband");
            list.add("Janjgir-Champa");
            list.add("Jashpur");
            list.add("Kabirdham (Kawardha)");
            list.add("Kanker (North Bastar)");
            list.add("Kondagaon");
            list.add("Korba");
            list.add("Korea (Koriya)");
            list.add("Mahasamund");
            list.add("Mungeli");
            list.add("Narayanpur");
            list.add("Raigarh");
            list.add("Raipur");
            list.add("Rajnandgaon");
            list.add("Sukma");
            list.add("Surajpur");
            list.add("Surguja");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);     }


        if(sp1.contentEquals("Dadra & Nagar Haveli (UT)")) {
            List<String> list = new ArrayList<String>();
            list.add("Dadra & Nagar Haveli");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);     }


        if(sp1.contentEquals("Daman and Diu (UT)")) {
            List<String> list = new ArrayList<String>();
            list.add("Daman");
            list.add("Diu");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);       }


        if(sp1.contentEquals("Delhi (NCT)")) {
            List<String> list = new ArrayList<String>();
            list.add("Central Delhi");
            list.add("East Delhi");
            list.add("New Delhi");
            list.add("North Delhi");
            list.add("North East Delhi");
            list.add("North West Delhi");
            list.add("Shahdara");
            list.add("South Delhi");
            list.add("South East Delhi");
            list.add("South West Delhi");
            list.add("West Delhi");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);      }


        if(sp1.contentEquals("Goa")) {
            List<String> list = new ArrayList<String>();
            list.add("North Goa");
            list.add("South Goa");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);       }


        if(sp1.contentEquals("Gujarat")) {
            List<String> list = new ArrayList<String>();
            list.add("Ahmedabad");
            list.add("Amreli");
            list.add("Anand");
            list.add("Aravalli");
            list.add("Banaskantha (Palanpur)");
            list.add("Bharuch");
            list.add("Bhavnagar");
            list.add("Botad");
            list.add("Chhota Udepur");
            list.add("Dahod");
            list.add("Dangs (Ahwa)");
            list.add("Devbhoomi Dwarka");
            list.add("Gandhinagar");
            list.add("Gir Somnath");
            list.add("Jamnagar");
            list.add("Junagadh");
            list.add("Kachchh");
            list.add("Kheda (Nadiad)");
            list.add("Mahisagar");
            list.add("Mehsana");
            list.add("Morbi");
            list.add("Narmada (Rajpipla)");
            list.add("Navsari");
            list.add("Panchmahal (Godhra)");
            list.add("Patan");
            list.add("Porbandar");
            list.add("Rajkot");
            list.add("Sabarkantha (Himmatnagar)");
            list.add("Surat");
            list.add("Surendranagar");
            list.add("Tapi (Vyara)");
            list.add("Vadodara");
            list.add("Valsad");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);       }


        if(sp1.contentEquals("Himachal Pradesh")) {
            List<String> list = new ArrayList<String>();
            list.add("Bilaspur");
            list.add("Chamba");
            list.add("Hamirpur");
            list.add("Kangra");
            list.add("Kinnaur");
            list.add("Kullu");
            list.add("Lahaul &amp; Spiti");
            list.add("Mandi");
            list.add("Shimla");
            list.add("Sirmaur (Sirmour)");
            list.add("Solan");
            list.add("Una");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);        }


        if(sp1.contentEquals("Jammu and Kashmir")) {
            List<String> list = new ArrayList<String>();
            list.add("Anantnag");
            list.add("Bandipore");
            list.add("Baramulla");
            list.add("Budgam");
            list.add("Doda");
            list.add("Ganderbal");
            list.add("Jammu");
            list.add("Kargil");
            list.add("Kathua");
            list.add("Kishtwar");
            list.add("Kulgam");
            list.add("Kupwara");
            list.add("Leh");
            list.add("Poonch");
            list.add("Pulwama");
            list.add("Rajouri");
            list.add("Ramban");
            list.add("Reasi");
            list.add("Samba");
            list.add("Shopian");
            list.add("Srinagar");
            list.add("Udhampur");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);        }


        if(sp1.contentEquals("Jharkhand")) {
            List<String> list = new ArrayList<String>();
            list.add("Bokaro");
            list.add("Chatra");
            list.add("Deoghar");
            list.add("Dhanbad");
            list.add("Dumka");
            list.add("East Singhbhum");
            list.add("Garhwa");
            list.add("Giridih");
            list.add("Godda");
            list.add("Gumla");
            list.add("Hazaribag");
            list.add("Jamtara");
            list.add("Khunti");
            list.add("Koderma");
            list.add("Latehar");
            list.add("Lohardaga");
            list.add("Pakur");
            list.add("Palamu");
            list.add("Ramgarh");
            list.add("Ranchi");
            list.add("Sahibganj");
            list.add("Seraikela-Kharsawan");
            list.add("Simdega");
            list.add("West Singhbhum");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);       }

        if(sp1.contentEquals("Karnataka")) {
            List<String> list = new ArrayList<String>();
            list.add("Bagalkot");
            list.add("Ballari (Bellary)");
            list.add("Belagavi (Belgaum)");
            list.add("Bengaluru (Bangalore) Rural");
            list.add("Bengaluru (Bangalore) Urban");
            list.add("Bidar");
            list.add("Chamarajanagar");
            list.add("Chikballapur");
            list.add("Chikkamagaluru (Chikmagalur)");
            list.add("Chitradurga");
            list.add("Dakshina Kannada");
            list.add("Davangere");
            list.add("Dharwad");
            list.add("Gadag");
            list.add("Hassan");
            list.add("Haveri");
            list.add("Kalaburagi (Gulbarga)");
            list.add("Kodagu");
            list.add("Kolar");
            list.add("Koppal");
            list.add("Mandya");
            list.add("Mysuru (Mysore)");
            list.add("Raichur");
            list.add("Ramanagara");
            list.add("Shivamogga (Shimoga)");
            list.add("Tumakuru (Tumkur)");
            list.add("Udupi");
            list.add("Uttara Kannada (Karwar)");
            list.add("Vijayapura (Bijapur)");
            list.add("Yadgir");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Kerala")) {
            List<String> list = new ArrayList<String>();
            list.add("Alappuzha");
            list.add("Ernakulam");
            list.add("Idukki");
            list.add("Kannur");
            list.add("Kasaragod");
            list.add("Kollam");
            list.add("Kottayam");
            list.add("Kozhikode");
            list.add("Malappuram");
            list.add("Palakkad");
            list.add("Pathanamthitta");
            list.add("Thiruvananthapuram");
            list.add("Thrissur");
            list.add("Wayanad");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Lakshadweep (UT)")) {
            List<String> list = new ArrayList<String>();
            list.add("Agatti");
            list.add("Amini");
            list.add("Androth");
            list.add("Bithra");
            list.add("Chethlath");
            list.add("Kavaratti");
            list.add("Kadmath");
            list.add("Kalpeni");
            list.add("Kilthan");
            list.add("Minicoy");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Madhya Pradesh")) {
            List<String> list = new ArrayList<String>();
            list.add("Agar Malwa");
            list.add("Alirajpur");
            list.add("Anuppur");
            list.add("Ashoknagar");
            list.add("Balaghat");
            list.add("Barwani");
            list.add("Betul");
            list.add("Bhind");
            list.add("Bhopal");
            list.add("Burhanpur");
            list.add("Chhatarpur");
            list.add("Chhindwara");
            list.add("Damoh");
            list.add("Datia");
            list.add("Dewas");
            list.add("Dhar");
            list.add("Dindori");
            list.add("Guna");
            list.add("Gwalior");
            list.add("Harda");
            list.add("Hoshangabad");
            list.add("Indore");
            list.add("Jabalpur");
            list.add("Jhabua");
            list.add("Katni");
            list.add("Khandwa");
            list.add("Khargone");
            list.add("Mandla");
            list.add("Mandsaur");
            list.add("Morena");
            list.add("Narsinghpur");
            list.add("Neemuch");
            list.add("Panna");
            list.add("Raisen");
            list.add("Rajgarh");
            list.add("Ratlam");
            list.add("Rewa");
            list.add("Sagar");
            list.add("Satna");
            list.add("Sehore");
            list.add("Seoni");
            list.add("Shahdol");
            list.add("Shajapur");
            list.add("Sheopur");
            list.add("Shivpuri");
            list.add("Sidhi");
            list.add("Singrauli");
            list.add("Tikamgarh");
            list.add("Ujjain");
            list.add("Umaria");
            list.add("Vidisha");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Maharashtra")) {
            List<String> list = new ArrayList<String>();
            list.add("Ahmednagar");
            list.add("Akola");
            list.add("Amravati");
            list.add("Aurangabad");
            list.add("Beed");
            list.add("Bhandara");
            list.add("Buldhana");
            list.add("Chandrapur");
            list.add("Dhule");
            list.add("Gadchiroli");
            list.add("Gondia");
            list.add("Hingoli");
            list.add("Jalgaon");
            list.add("Jalna");
            list.add("Kolhapur");
            list.add("Latur");
            list.add("Mumbai City");
            list.add("Mumbai Suburban");
            list.add("Nagpur");
            list.add("Nanded");
            list.add("Nandurbar");
            list.add("Nashik");
            list.add("Osmanabad");
            list.add("Palghar");
            list.add("Parbhani");
            list.add("Pune");
            list.add("Raigad");
            list.add("Ratnagiri");
            list.add("Sangli");
            list.add("Satara");
            list.add("Sindhudurg");
            list.add("Solapur");
            list.add("Thane");
            list.add("Wardha");
            list.add("Washim");
            list.add("Yavatmal");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Manipur")) {
            List<String> list = new ArrayList<String>();
            list.add("Bishnupur");
            list.add("Chandel");
            list.add("Churachandpur");
            list.add("Imphal East");
            list.add("Imphal West");
            list.add("Jiribam");
            list.add("Kakching");
            list.add("Kamjong");
            list.add("Kangpokpi");
            list.add("Noney");
            list.add("Pherzawl");
            list.add("Senapati");
            list.add("Tamenglong");
            list.add("Tengnoupal");
            list.add("Thoubal");
            list.add("Ukhrul");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Meghalaya")) {
            List<String> list = new ArrayList<String>();
            list.add("East Garo Hills");
            list.add("East Jaintia Hills");
            list.add("East Khasi Hills");
            list.add("North Garo Hills");
            list.add("Ri Bhoi");
            list.add("South Garo Hills");
            list.add("South West Garo Hills ");
            list.add("South West Khasi Hills");
            list.add("West Garo Hills");
            list.add("West Jaintia Hills");
            list.add("West Khasi Hills");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Mizoram")) {
            List<String> list = new ArrayList<String>();
            list.add("Aizawl");
            list.add("Champhai");
            list.add("Kolasib");
            list.add("Lawngtlai");
            list.add("Lunglei");
            list.add("Mamit");
            list.add("Saiha");
            list.add("Serchhip");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Nagaland")) {
            List<String> list = new ArrayList<String>();
            list.add("Dimapur");
            list.add("Kiphire");
            list.add("Kohima");
            list.add("Longleng");
            list.add("Mokokchung");
            list.add("Mon");
            list.add("Peren");
            list.add("Phek");
            list.add("Tuensang");
            list.add("Wokha");
            list.add("Zunheboto");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Odisha")) {
            List<String> list = new ArrayList<String>();
            list.add("Angul");
            list.add("Balangir");
            list.add("Balasore");
            list.add("Bargarh");
            list.add("Bhadrak");
            list.add("Boudh");
            list.add("Cuttack");
            list.add("Deogarh");
            list.add("Dhenkanal");
            list.add("Gajapati");
            list.add("Ganjam");
            list.add("Jagatsinghapur");
            list.add("Jajpur");
            list.add("Jharsuguda");
            list.add("Kalahandi");
            list.add("Kandhamal");
            list.add("Kendrapara");
            list.add("Kendujhar (Keonjhar)");
            list.add("Khordha");
            list.add("Koraput");
            list.add("Malkangiri");
            list.add("Mayurbhanj");
            list.add("Nabarangpur");
            list.add("Nayagarh");
            list.add("Nuapada");
            list.add("Puri");
            list.add("Rayagada");
            list.add("Sambalpur");
            list.add("Sonepur");
            list.add("Sundargarh");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }


        if(sp1.contentEquals("Puducherry (UT)")) {
            List<String> list = new ArrayList<String>();
            list.add("Karaikal");
            list.add("Mahe");
            list.add("Pondicherry");
            list.add("Yanam");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);     }

        if(sp1.contentEquals("Punjab")) {
            List<String> list = new ArrayList<String>();
            list.add("Amritsar");
            list.add("Barnala");
            list.add("Bathinda");
            list.add("Faridkot");
            list.add("Fatehgarh Sahib");
            list.add("Fazilka");
            list.add("Ferozepur");
            list.add("Gurdaspur");
            list.add("Hoshiarpur");
            list.add("Jalandhar");
            list.add("Kapurthala");
            list.add("Ludhiana");
            list.add("Mansa");
            list.add("Moga");
            list.add("Muktsar");
            list.add("Nawanshahr (Shahid Bhagat Singh Nagar)");
            list.add("Pathankot");
            list.add("Patiala");
            list.add("Rupnagar");
            list.add("Sahibzada Ajit Singh Nagar (Mohali)");
            list.add("Sangrur");
            list.add("Tarn Taran");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Rajasthan")) {
            List<String> list = new ArrayList<String>();
            list.add("Ajmer");
            list.add("Alwar");
            list.add("Banswara");
            list.add("Baran");
            list.add("Barmer");
            list.add("Bharatpur");
            list.add("Bhilwara");
            list.add("Bikaner");
            list.add("Bundi");
            list.add("Chittorgarh");
            list.add("Churu");
            list.add("Dausa");
            list.add("Dholpur");
            list.add("Dungarpur");
            list.add("Hanumangarh");
            list.add("Jaipur");
            list.add("Jaisalmer");
            list.add("Jalore");
            list.add("Jhalawar");
            list.add("Jhunjhunu");
            list.add("Jodhpur");
            list.add("Karauli");
            list.add("Kota");
            list.add("Nagaur");
            list.add("Pali");
            list.add("Pratapgarh");
            list.add("Rajsamand");
            list.add("Sawai Madhopur");
            list.add("Sikar");
            list.add("Sirohi");
            list.add("Sri Ganganagar");
            list.add("Tonk");
            list.add("Udaipur");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Sikkim")) {
            List<String> list = new ArrayList<String>();
            list.add("East Sikkim");
            list.add("North Sikkim");
            list.add("South Sikkim");
            list.add("West Sikkim");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Tamil Nadu")) {
            List<String> list = new ArrayList<String>();
            list.add("Ariyalur");
            list.add("Chennai");
            list.add("Coimbatore");
            list.add("Cuddalore");
            list.add("Dharmapuri");
            list.add("Dindigul");
            list.add("Erode");
            list.add("Kanchipuram");
            list.add("Kanyakumari");
            list.add("Karur");
            list.add("Krishnagiri");
            list.add("Madurai");
            list.add("Nagapattinam");
            list.add("Namakkal");
            list.add("Nilgiris");
            list.add("Perambalur");
            list.add("Pudukkottai");
            list.add("Ramanathapuram");
            list.add("Salem");
            list.add("Sivaganga");
            list.add("Thanjavur");
            list.add("Theni");
            list.add("Thoothukudi (Tuticorin)");
            list.add("Tiruchirappalli");
            list.add("Tirunelveli");
            list.add("Tiruppur");
            list.add("Tiruvallur");
            list.add("Tiruvannamalai");
            list.add("Tiruvarur");
            list.add("Vellore");
            list.add("Viluppuram");
            list.add("Virudhunagar");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Telangana")) {
            List<String> list = new ArrayList<String>();
            list.add("Adilabad");
            list.add("Bhadradri Kothagudem");
            list.add("Hyderabad");
            list.add("Jagtial");
            list.add("Jangaon");
            list.add("Jayashankar Bhoopalpally");
            list.add("Jogulamba Gadwal");
            list.add("Kamareddy");
            list.add("Karimnagar");
            list.add("Khammam");
            list.add("Komaram Bheem Asifabad");
            list.add("Mahabubabad");
            list.add("Mahabubnagar");
            list.add("Mancherial");
            list.add("Medak");
            list.add("Medchal");
            list.add("Nagarkurnool");
            list.add("Nalgonda");
            list.add("Nirmal");
            list.add("Nizamabad");
            list.add("Peddapalli");
            list.add("Rajanna Sircilla");
            list.add("Rangareddy");
            list.add("Sangareddy");
            list.add("Siddipet");
            list.add("Suryapet");
            list.add("Vikarabad");
            list.add("Wanaparthy");
            list.add("Warangal (Rural)");
            list.add("Warangal (Urban)");
            list.add("Yadadri Bhuvanagiri");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Tripura")) {
            List<String> list = new ArrayList<String>();
            list.add("Dhalai");
            list.add("Gomati");
            list.add("Khowai");
            list.add("North Tripura");
            list.add("Sepahijala");
            list.add("South Tripura");
            list.add("Unakoti");
            list.add("West Tripura");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("Uttarakhand")) {
            List<String> list = new ArrayList<String>();
            list.add("Almora");
            list.add("Bageshwar");
            list.add("Chamoli");
            list.add("Champawat");
            list.add("Dehradun");
            list.add("Haridwar");
            list.add("Nainital");
            list.add("Pauri Garhwal");
            list.add("Pithoragarh");
            list.add("Rudraprayag");
            list.add("Tehri Garhwal");
            list.add("Udham Singh Nagar");
            list.add("Uttarkashi");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }


        if(sp1.contentEquals("Uttar Pradesh")) {
            List<String> list = new ArrayList<String>();
            list.add("Agra");
            list.add("Aligarh");
            list.add("Allahabad");
            list.add("Ambedkar Nagar");
            list.add("Amethi (Chatrapati Sahuji Mahraj Nagar)");
            list.add("Amroha (J.P. Nagar)");
            list.add("Auraiya");
            list.add("Azamgarh");
            list.add("Baghpat");
            list.add("Bahraich");
            list.add("Ballia");
            list.add("Balrampur");
            list.add("Banda");
            list.add("Barabanki");
            list.add("Bareilly");
            list.add("Basti");
            list.add("Bhadohi");
            list.add("Bijnor");
            list.add("Budaun");
            list.add("Bulandshahr");
            list.add("Chandauli");
            list.add("Chitrakoot");
            list.add("Deoria");
            list.add("Etah");
            list.add("Etawah");
            list.add("Faizabad");
            list.add("Farrukhabad");
            list.add("Fatehpur");
            list.add("Firozabad");
            list.add("Gautam Buddha Nagar");
            list.add("Ghaziabad");
            list.add("Ghazipur");
            list.add("Gonda");
            list.add("Gorakhpur");
            list.add("Hamirpur");
            list.add("Hapur (Panchsheel Nagar)");
            list.add("Hardoi");
            list.add("Hathras");
            list.add("Jalaun");
            list.add("Jaunpur");
            list.add("Jhansi");
            list.add("Kannauj");
            list.add("Kanpur Dehat");
            list.add("Kanpur Nagar");
            list.add("Kanshiram Nagar (Kasganj)");
            list.add("Kaushambi");
            list.add("Kushinagar (Padrauna)");
            list.add("Lakhimpur - Kheri");
            list.add("Lalitpur");
            list.add("Lucknow");
            list.add("Maharajganj");
            list.add("Mahoba");
            list.add("Mainpuri");
            list.add("Mathura");
            list.add("Mau");
            list.add("Meerut");
            list.add("Mirzapur");
            list.add("Moradabad");
            list.add("Muzaffarnagar");
            list.add("Pilibhit");
            list.add("Pratapgarh");
            list.add("RaeBareli");
            list.add("Rampur");
            list.add("Saharanpur");
            list.add("Sambhal (Bhim Nagar)");
            list.add("Sant Kabir Nagar");
            list.add("Shahjahanpur");
            list.add("Shamali (Prabuddh Nagar)");
            list.add("Shravasti");
            list.add("Siddharth Nagar");
            list.add("Sitapur");
            list.add("Sonbhadra");
            list.add("Sultanpur");
            list.add("Unnao");
            list.add("Varanasi");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);
        }

        if(sp1.contentEquals("West Bengal")) {
            List<String> list = new ArrayList<String>();
            list.add("Alipurduar");
            list.add("Bankura");
            list.add("Birbhum");
            list.add("Burdwan (Bardhaman)");
            list.add("Cooch Behar");
            list.add("Dakshin Dinajpur (South Dinajpur)");
            list.add("Darjeeling");
            list.add("Hooghly");
            list.add("Howrah");
            list.add("Jalpaiguri");
            list.add("Kalimpong");
            list.add("Kolkata");
            list.add("Malda");
            list.add("Murshidabad");
            list.add("Nadia");
            list.add("North 24 Parganas");
            list.add("Paschim Medinipur (West Medinipur)");
            list.add("Purba Medinipur (East Medinipur)");
            list.add("Purulia");
            list.add("South 24 Parganas");
            list.add("Uttar Dinajpur (North Dinajpur)");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            mDistrict.setAdapter(dataAdapter);

        }


        state = mState.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
