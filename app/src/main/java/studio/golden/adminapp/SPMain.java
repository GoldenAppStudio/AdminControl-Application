package studio.golden.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.Arrays;

public class SPMain extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spmain);

        progressDialog = new ProgressDialog(SPMain.this);

        progressDialog.setMessage("Loading Data from Database");

        progressDialog.show();


        String full_link = "Service/" + Chooser.key + "/" + Chooser.sub_service + "/" + Chooser.province + "/" + Chooser.district[0] + "/" + RecyclerViewAdapter.sp_uid;

        EditText name = findViewById(R.id.namE);
        EditText email = findViewById(R.id.emaiL);
        EditText priority = findViewById(R.id.prioritY);
        EditText phone = findViewById(R.id.phonE);
        EditText description = findViewById(R.id.descriptioN);
        EditText address = findViewById(R.id.addresS);
        EditText price = findViewById(R.id.pricE);

        Button button = findViewById(R.id.sb);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(full_link);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                price.setText(dataSnapshot.child("price").getValue().toString());
                priority.setText(dataSnapshot.child("priority").getValue().toString());
                phone.setText(dataSnapshot.child("phone").getValue().toString());
                description.setText(dataSnapshot.child("description").getValue().toString());
                address.setText(dataSnapshot.child("address").getValue().toString());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(v -> {
            progressDialog.setMessage("Updating Data in Database");

            progressDialog.show();

            databaseReference.child("name").setValue(name.getText().toString());
            databaseReference.child("description").setValue(description.getText().toString());
            databaseReference.child("phone").setValue(phone.getText().toString());
            databaseReference.child("price").setValue(price.getText().toString());
            databaseReference.child("priority").setValue(priority.getText().toString());
            databaseReference.child("email").setValue(email.getText().toString());
            databaseReference.child("address").setValue(address.getText().toString());

            progressDialog.dismiss();
            Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SPMain.this, SPEdit.class);
            startActivity(intent);
            finish();
        });
    }
}
