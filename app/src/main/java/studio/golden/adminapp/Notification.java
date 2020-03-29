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

public class Notification extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setTitle("Notification");

        progressDialog = new ProgressDialog(Notification.this);

        progressDialog.setMessage("Loading Data from Database");

        progressDialog.show();

        Button okay = findViewById(R.id.cnn);
        final EditText email = findViewById(R.id.bbc);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference ref = database.getReference("Notification/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child("text").exists()) {
                    email.setText(snapshot.child("text").getValue().toString());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        okay.setOnClickListener(v -> {
            ref.child("text").setValue("" + email.getText().toString());
            Toast.makeText(Notification.this, "Data successfully updated", Toast.LENGTH_SHORT).show();
        });
    }
}
