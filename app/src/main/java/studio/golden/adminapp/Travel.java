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

public class Travel extends AppCompatActivity {

    private EditText train, flight, hotel, taxi, ship, trip;
    private Button button;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        getSupportActionBar().setTitle("Travel");

        progressDialog = new ProgressDialog(Travel.this);

        progressDialog.setMessage("Loading Data from Database");

        progressDialog.show();


        button = findViewById(R.id.okT);
        train = findViewById(R.id.trainX);
        flight = findViewById(R.id.flightX);
        hotel = findViewById(R.id.hotelX);
        taxi = findViewById(R.id.taxiX);
        ship = findViewById(R.id.shipX);
        trip = findViewById(R.id.tripX);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Travel");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                train.setText(dataSnapshot.child("train").getValue().toString());
                ship.setText(dataSnapshot.child("ship").getValue().toString());
                flight.setText(dataSnapshot.child("plane").getValue().toString());
                taxi.setText(dataSnapshot.child("cab").getValue().toString());
                trip.setText(dataSnapshot.child("trip").getValue().toString());
                hotel.setText(dataSnapshot.child("hotel").getValue().toString());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(v -> {
            databaseReference.child("train").setValue(train.getText().toString());
            databaseReference.child("cab").setValue(taxi.getText().toString());
            databaseReference.child("plane").setValue(flight.getText().toString());
            databaseReference.child("hotel").setValue(hotel.getText().toString());
            databaseReference.child("trip").setValue(trip.getText().toString());
            databaseReference.child("ship").setValue(ship.getText().toString());

            Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Travel.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
