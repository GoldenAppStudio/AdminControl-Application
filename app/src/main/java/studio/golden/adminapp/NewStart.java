package studio.golden.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewStart extends AppCompatActivity {

    private int count;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_start);

        final EditText start = findViewById(R.id.startNS);
        final Button button = findViewById(R.id.submitNS);
        final EditText end = findViewById(R.id.endNS);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("BusRoute/");

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
               if(start.getText().toString().isEmpty() || end.getText().toString().isEmpty()) {
                   Toast.makeText(this, "Please insert start And/or destination", Toast.LENGTH_SHORT).show();
                   return;
               }

            progressDialog = new ProgressDialog(NewStart.this);
            progressDialog.setMessage("Adding service to database...");
            progressDialog.show();

            ref.child(String.valueOf(count + 1)).child("start").setValue("" + start.getText().toString());
            ref.child(String.valueOf(count + 1)).child("end").child("1").child("name").setValue("" + end.getText().toString());

            Toast.makeText(this, "Route Added", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(NewStart.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NewStart.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
