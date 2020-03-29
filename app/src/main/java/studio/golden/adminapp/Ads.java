package studio.golden.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Ads extends AppCompatActivity {

    String value;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    String Database_Path;
    public static int Abc;
    List<AdDetails> list = new ArrayList<>();

    RecyclerView recyclerView;
    AdsAdapter adapter ;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        getSupportActionBar().setTitle("Ads Providers...");

        search = findViewById(R.id.searchX);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(Ads.this));

        progressDialog = new ProgressDialog(Ads.this);

        progressDialog.setMessage("Loading Data from Database");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ShowAd/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Abc = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("ShowAd/");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    AdDetails studentDetails = dataSnapshot.getValue(AdDetails.class);

                    list.add(studentDetails);

                }

                adapter = new AdsAdapter(Ads.this, list);

                recyclerView.setAdapter(adapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();

            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Ads.this, MainActivity.class));
        finish();

    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<AdDetails> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (AdDetails s : list) {
            //if the existing elements contains the search input
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }
        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }
}
