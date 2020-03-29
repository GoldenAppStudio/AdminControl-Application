package studio.golden.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BusPlace extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText dName, dNo, cName, cNo, eT, sT, fairB;
    private Button button;
    private Spinner startB, endB, type;
    private ProgressDialog progressDialog;
    List<String> c = new ArrayList<>();
    private int countL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_place);

        dName = findViewById(R.id.dName);
        dNo = findViewById(R.id.dNo);
        cName = findViewById(R.id.cName);
        cNo = findViewById(R.id.cNo);
        sT = findViewById(R.id.sT);
        eT = findViewById(R.id.eT);
        startB = findViewById(R.id.startB);
        endB = findViewById(R.id.endB);
        type = findViewById(R.id.type);
        fairB = findViewById(R.id.fairB);
        button = findViewById(R.id.okB);


        startB.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        List<String> t = new ArrayList<>();
        t.add("Roadways");
        t.add("Private");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BusPlace.this, android.R.layout.simple_spinner_item, t);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(dataAdapter);
        progressDialog = new ProgressDialog(BusPlace.this);
        progressDialog.setMessage("fetching data...");
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BusRoute/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    categories.add("" + dataSnapshot.child("start").getValue().toString());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BusPlace.this, android.R.layout.simple_spinner_item, categories);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    startB.setAdapter(dataAdapter);
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(v -> {
            if (fairB.getText().toString().isEmpty() || eT.getText().toString().isEmpty() || sT.getText().toString().isEmpty()) {

                Toast.makeText(BusPlace.this, "Please insert all with mark *", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog = new ProgressDialog(BusPlace.this);
            progressDialog.setMessage("Adding service to database...");
            progressDialog.show();

            final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Bus/" + startB.getSelectedItem().toString() + "/" + endB.getSelectedItem().toString());

            db.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    countL = (int) dataSnapshot.getChildrenCount();
                    db.child(String.valueOf(countL + 1)).child("dname").setValue("" + dName.getText().toString());
                    db.child(String.valueOf(countL + 1)).child("dno").setValue("" + dNo.getText().toString());
                    db.child(String.valueOf(countL + 1)).child("cname").setValue("" + cName.getText().toString());
                    db.child(String.valueOf(countL + 1)).child("fair").setValue("" + fairB.getText().toString());
                    db.child(String.valueOf(countL + 1)).child("cno").setValue("" + cNo.getText().toString());
                    db.child(String.valueOf(countL + 1)).child("source").setValue("" + startB.getSelectedItem().toString());
                    db.child(String.valueOf(countL + 1)).child("destination").setValue("" + endB.getSelectedItem().toString());
                    db.child(String.valueOf(countL + 1)).child("start").setValue("" + sT.getText().toString());
                    db.child(String.valueOf(countL + 1)).child("end").setValue("" + eT.getText().toString());
                    db.child(String.valueOf(countL + 1)).child("uid").setValue("" + String.valueOf(countL + 1));
                    db.child(String.valueOf(countL + 1)).child("name").setValue("" + type.getSelectedItem().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            progressDialog.dismiss();

            Toast.makeText(this, "Route Added", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BusPlace.this, MainActivity.class);
            startActivity(intent);
            finish();

        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        c.clear();
        progressDialog = new ProgressDialog(BusPlace.this);
        progressDialog.setMessage("fetching data...");
        progressDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BusRoute/" + String.valueOf(position + 1) + "/end/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    c.add("" + dataSnapshot.child("name").getValue().toString());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BusPlace.this, android.R.layout.simple_spinner_item, c);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    endB.setAdapter(dataAdapter);
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
