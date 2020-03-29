package studio.golden.adminapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DeleteSubService extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner serviceSpinner;
    private Spinner subServiceSpinner;
    AtomicReference<ProgressDialog> progressDialog;
    private int servicePosition = 0, subServicePosition = 0, totalCount = 0;

    List<String> serviceList = new ArrayList<>();
    List<String> subServiceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sub_service);

        serviceSpinner = findViewById(R.id.dServiceSpinner);
        subServiceSpinner = findViewById(R.id.dSubServiceSpinner);
        Button delete = findViewById(R.id.deleteSubService);
        progressDialog = new AtomicReference<>(new ProgressDialog(DeleteSubService.this));
        serviceSpinner.setOnItemSelectedListener(this);

        populate_service_spinner(); // To add entry to service spinner

       delete.setOnClickListener(v -> delete_sub_service()); // Delete button OnClick action

    }

    public void populate_service_spinner(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ServiceList/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.child("service").getValue().toString().equals("Travel")){
                        continue;
                    }
                    serviceList.add("" + dataSnapshot.child("service").getValue().toString());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(DeleteSubService.this, android.R.layout.simple_spinner_item, serviceList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    serviceSpinner.setAdapter(dataAdapter);
                    progressDialog.get().dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Delete the sub-service
    public void delete_sub_service(){
        // Alert of confirmation
        subServicePosition = subServiceSpinner.getSelectedItemPosition() + 1;
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("If you proceed then its nested data will also be removed. It can not be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Proceed", (dialog, which) -> {
                    DeleteSubService.super.onBackPressed();
                    progressDialog.set(new ProgressDialog(DeleteSubService.this));
                    progressDialog.get().setMessage("Removing from database...");
                    progressDialog.get().show();
                    // Remove the entry from db
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ServiceList/" + servicePosition + "/")
                            .child("subService").child(String.valueOf(subServicePosition));
                    databaseReference.removeValue();

                    adjust_database();  // To adjust the database back to norm

                    progressDialog.get().dismiss();
                    Toast.makeText(this, "Sub-Service removed from database", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DeleteSubService.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }).create().show();
    }

    // To adjust the database back to norm
    public void adjust_database(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ServiceList/"
                + servicePosition + "/").child("subService");
        if (totalCount > subServicePosition){
            for(int i = 0; i < totalCount - subServicePosition; i++){
                int finalI = i;
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ref.child(String.valueOf(subServicePosition + finalI)).setValue(dataSnapshot.child(String.valueOf(subServicePosition + finalI + 1)).getValue());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            // Delete the last entry in db as it has been duplicated and moved up by one position
            DatabaseReference PostReference = FirebaseDatabase.getInstance().getReference("ServiceList/"
                    + servicePosition + "/").child("subService").child(String.valueOf(totalCount));
            PostReference.removeValue();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        servicePosition = position + 2; // count = position of service in database
        populate_sub_service_spinner(servicePosition);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public void populate_sub_service_spinner(int position){
        subServiceList.clear(); // Clear old entries from the list
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(
                "ServiceList/" + position + "/subService");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                totalCount = (int) snapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    subServiceList.add("" + dataSnapshot.child("name").getValue().toString());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(DeleteSubService.this, android.R.layout.simple_spinner_item, subServiceList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subServiceSpinner.setAdapter(dataAdapter);
                    progressDialog.get().dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
