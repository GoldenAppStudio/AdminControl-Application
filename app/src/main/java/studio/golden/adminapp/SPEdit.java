package studio.golden.adminapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SPEdit extends AppCompatActivity {

    String value;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    String Database_Path;
    public static String Abc;
    List<ServiceProviderClass> list = new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter ;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spedit);


        getSupportActionBar().setTitle("Service Providers...");

        search = findViewById(R.id.searchX);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(SPEdit.this));

        progressDialog = new ProgressDialog(SPEdit.this);

        progressDialog.setMessage("Loading Data from Database");

        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference(Chooser.linkX);

        Query myMostViewedPostsQuery = databaseReference.orderByChild("priority");
        myMostViewedPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ServiceProviderClass studentDetails = dataSnapshot.getValue(ServiceProviderClass.class);

                    list.add(studentDetails);

                }

                adapter = new RecyclerViewAdapter(SPEdit.this, list);

                ItemTouchHelper.Callback callback =
                        new ItemMoveCallback(adapter);
                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                touchHelper.attachToRecyclerView(recyclerView);

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
        startActivity(new Intent(SPEdit.this, MainActivity.class));
        finish();

    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<ServiceProviderClass> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ServiceProviderClass s : list) {
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
