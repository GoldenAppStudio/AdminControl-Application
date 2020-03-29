package studio.golden.adminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Attendance extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        getSupportActionBar().setTitle("Contact");

        progressDialog = new ProgressDialog(Attendance.this);

        progressDialog.setMessage("Loading Data from Database");

        progressDialog.show();

        Button okay = findViewById(R.id.okV);
        final EditText email = findViewById(R.id.emailV);
        final EditText phone = findViewById(R.id.phoneV);
        final EditText address = findViewById(R.id.addressV);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference ref = database.getReference("Contacts/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child("email").exists() && snapshot.child("phone").exists() && snapshot.child("address").exists()) {

                    email.setText(snapshot.child("email").getValue().toString());
                    phone.setText(snapshot.child("phone").getValue().toString());
                    address.setText(snapshot.child("address").getValue().toString());
                    progressDialog.dismiss();
                } else {
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
            ref.child("address").setValue("" + address.getText().toString());
            Toast.makeText(Attendance.this, "Data successfully updated", Toast.LENGTH_SHORT).show();
        });


    }
}
