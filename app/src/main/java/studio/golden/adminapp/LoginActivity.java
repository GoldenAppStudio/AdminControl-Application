package studio.golden.adminapp;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    public Button btn_login;
    EditText j, g;
    private ProgressDialog progressDialog;
    String email, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.login);
        g = findViewById(R.id.g);
        j = findViewById(R.id.j);

        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(i);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("AdminLogin");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                email = snapshot.child("user").getValue().toString();
                pass = snapshot.child("pass").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_login.setOnClickListener(v -> {
            if(TextUtils.isEmpty(j.getText().toString()) || TextUtils.isEmpty(g.getText().toString())){
                Toast.makeText(this, "Please enter username or password", Toast.LENGTH_LONG).show();
            } else {
                if(j.getText().toString().equals(pass) && g.getText().toString().equals(email)){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "username or password does not match", Toast.LENGTH_LONG).show();

                }
            }

        });
    }
}
