package studio.golden.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SPShow extends AppCompatActivity {

    TextView showName, showPrice, showEmail, showPhone, showDisc, showState, showDistric, showService, showAddress;
    Button allow, deny;
    String mName, mPhone, mDescription, mPrice, dist, state, service, mEmail, mAddress;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    String uid, uuid;
    private CircleImageView setupImage;
    private ProgressDialog progressDialog;
    String sub, log;
    private static int totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spshow);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uid = extras.getString("uid");
            //The key argument here must match that used in the other activity
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference[] gsReference = new StorageReference[1];


        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        showDisc = findViewById(R.id.showDisc);
        showName = findViewById(R.id.showName);
        showPhone = findViewById(R.id.showPhone);
        showState = findViewById(R.id.showState);
        showPrice = findViewById(R.id.showPrice);
        showDistric = findViewById(R.id.showDistric);
        showEmail = findViewById(R.id.showEmail);
        showService = findViewById(R.id.showService);
        setupImage = findViewById(R.id.showImage);
        showAddress = findViewById(R.id.showAddress);

        allow = findViewById(R.id.allow);
        deny = findViewById(R.id.deny);
        Log.d("phoneID", "UID: " + uid);
        firebaseFirestore.collection("Users").document(uid).get().addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                if(task.getResult().exists()){

                    mName = task.getResult().getString("name");
                    mPhone = task.getResult().getString("phone");
                    mDescription = task.getResult().getString("description");
                    mEmail = task.getResult().getString("email");
                    mPrice = task.getResult().getString("price");
                    String image = task.getResult().getString("image");
                    state = task.getResult().getString("state");
                    dist = task.getResult().getString("district");
                    service = task.getResult().getString("service");
                    mAddress = task.getResult().getString("address");
                    uuid = task.getResult().getString("uid");
                    sub = task.getResult().getString("sub");
                    log = task.getResult().getString("login");
                    Log.d("firestore", "pathString: Service/" + service + "/" + sub + "/" + state + "/" + dist);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Service/").child(service).child(sub).child(state).child(dist);
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            totalCount = (int) snapshot.getChildrenCount();
                            Log.d("First time Log", "totalCount: " + totalCount);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    gsReference[0] = storage.getReferenceFromUrl("gs://serviceapp-67984.appspot.com/service-provider/"+ task.getResult().getString("service")+task.getResult().getString("sub")+ task.getResult().getString("state")+ task.getResult().getString("district") + uuid + ".jpg");

                    gsReference[0].getDownloadUrl().addOnSuccessListener(uri -> {


                        Glide.with(SPShow.this).load(uri.toString()).into(setupImage);

                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                    });
                    showDisc.setText(mDescription);
                    showEmail.setText(mEmail);
                    showPhone.setText(mPhone);
                    showPrice.setText(mPrice);
                    showName.setText(mName);
                    showDistric.setText(dist);
                    showState.setText(state);
                    showService.setText(service);
                    showAddress.setText(mAddress);

                }

            } else {

                String error = task.getException().getMessage();
                Toast.makeText(SPShow.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

            }

        });


        allow.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(SPShow.this);

            progressDialog.setMessage("Loading Data from Database");

            progressDialog.show();

            Log.d("insideAllow", "totalCount: " + totalCount);
            FirebaseDatabase database =  FirebaseDatabase.getInstance();
            DatabaseReference mRef =  database.getReference().child("Service").child(service).child(sub).child(state).child(dist).child(String.valueOf(totalCount + 1));
            mRef.child("phone").setValue(mPhone);
            mRef.child("name").setValue(mName);
            mRef.child("address").setValue(mAddress);
            mRef.child("description").setValue(mDescription);
            mRef.child("email").setValue(mEmail);
            mRef.child("district").setValue(dist);
            mRef.child("state").setValue(state);
            mRef.child("uid").setValue(String.valueOf(totalCount + 1));
            mRef.child("priority").setValue(String.valueOf(totalCount + 1));
            mRef.child("price").setValue(mPrice);
            mRef.child("service").setValue(service);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AutoSP").child(log);
            databaseReference.child("login_id").setValue(log);
            databaseReference.child("service").setValue(service);
            databaseReference.child("state").setValue(state);
            databaseReference.child("district").setValue(dist);
            databaseReference.child("sub").setValue(sub);
            databaseReference.child("uid").setValue(String.valueOf(totalCount + 1));
            Log.d("pathString3", "pathString: Service/" + service + "/" + sub + "/" + state + "/" + dist);


            progressDialog.dismiss();

            firebaseFirestore.collection("Users").document(uid).delete();
            Toast.makeText(SPShow.this, "Service Provider entry granted", Toast.LENGTH_LONG).show();
            Intent abcd = new Intent(SPShow.this, MainActivity.class);
            startActivity(abcd);
            finish();

        });

        deny.setOnClickListener(v -> {
            firebaseFirestore.collection("Users").document(uid).delete();
            Toast.makeText(SPShow.this, "Service Provider entry denied", Toast.LENGTH_LONG).show();
            Intent abcd = new Intent(SPShow.this, MainActivity.class);
            startActivity(abcd);
            finish();
        });

    }
}
