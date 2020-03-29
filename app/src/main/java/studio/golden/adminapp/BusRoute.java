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

public class BusRoute extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ProgressDialog progressDialog;
    private int countT;
    private int countL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        final EditText end = findViewById(R.id.endBR);
        final Button button = findViewById(R.id.submitBR);
        final Button button2 = findViewById(R.id.newStart);
        final Spinner spinner = findViewById(R.id.spinnerBR);

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        progressDialog = new ProgressDialog(BusRoute.this);
        progressDialog.setMessage("fetching data...");
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BusRoute/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    categories.add("" + dataSnapshot.child("start").getValue().toString());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(BusRoute.this, android.R.layout.simple_spinner_item, categories);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        button.setOnClickListener(v -> {

            if (end.getText().toString().isEmpty()) {

                Toast.makeText(BusRoute.this, "Please insert destination", Toast.LENGTH_SHORT).show();
                return;
            }

                progressDialog = new ProgressDialog(BusRoute.this);
                progressDialog.setMessage("Adding service to database...");
                progressDialog.show();

                final DatabaseReference db = database.getReference("BusRoute/" + String.valueOf(countT) + "/end/");

                db.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    countL = (int) dataSnapshot.getChildrenCount();
                    db.child(String.valueOf(countL + 1)).child("name").setValue("" + end.getText().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Toast.makeText(this, "Route Added", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BusRoute.this, MainActivity.class);
            startActivity(intent);
            finish();

        });

        button2.setOnClickListener(v -> {
            Intent intent = new Intent(BusRoute.this, NewStart.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        countT = position + 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
