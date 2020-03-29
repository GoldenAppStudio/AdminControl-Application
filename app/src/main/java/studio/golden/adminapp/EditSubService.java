package studio.golden.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class EditSubService extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button submit, delete, next;
    TextView f, a;
    Spinner sS, sSS;
    CircleImageView circleImageView;
    private ProgressDialog progressDialog;
    public static Uri mainImageURI = null;
    private boolean isChanged = false;
    private Bitmap compressedImageFile;
    private StorageReference storageReference;
    int totalCount;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sub_service);

        submit = findViewById(R.id.sSub);
        next = findViewById(R.id.sN);
        delete = findViewById(R.id.sD);
        sS = findViewById(R.id.sS);
        sSS = findViewById(R.id.sSS);
        f = findViewById(R.id.sF);
        a = findViewById(R.id.sC);
        circleImageView = findViewById(R.id.sI);

        sS.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("ServiceList/");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ServiceList/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    totalCount = (int) snapshot.getChildrenCount();
                    categories.add("" + dataSnapshot.child("service").getValue().toString());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditSubService.this, android.R.layout.simple_spinner_item, categories);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sS.setAdapter(dataAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        next.setOnClickListener(v -> {
              f.setVisibility(View.INVISIBLE);
              sS.setVisibility(View.GONE);
              sSS.setVisibility(View.GONE);
              next.setVisibility(View.GONE);
              a.setVisibility(View.VISIBLE);
              circleImageView.setVisibility(View.VISIBLE);
              delete.setVisibility(View.VISIBLE);
              submit.setVisibility(View.VISIBLE);



            progressDialog = new ProgressDialog(EditSubService.this);

            progressDialog.setMessage("Loading Data from Database");

            progressDialog.show();
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
