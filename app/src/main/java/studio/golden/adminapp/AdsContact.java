package studio.golden.adminapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

public class AdsContact extends AppCompatActivity {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_contact);

        getSupportActionBar().setTitle("AdsContact");

        progressDialog = new ProgressDialog(AdsContact.this);

        progressDialog.setMessage("Loading Data from Database");

        progressDialog.show();

        Button okay = findViewById(R.id.okayAd);
        final EditText email = findViewById(R.id.emailAd);
        final EditText phone = findViewById(R.id.phoneAd);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference ref = database.getReference("ContactDetails/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.child("email").exists() && snapshot.child("phone").exists()) {
                        email.setText(snapshot.child("email").getValue().toString());
                        phone.setText(snapshot.child("phone").getValue().toString());
                        progressDialog.dismiss();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        okay.setOnClickListener(v -> {
            ref.child("email").setValue("" + email.getText().toString());
            ref.child("phone").setValue("" + phone.getText().toString());
            Toast.makeText(AdsContact.this, "Data successfully updated", Toast.LENGTH_SHORT).show();
        });
    }
}
