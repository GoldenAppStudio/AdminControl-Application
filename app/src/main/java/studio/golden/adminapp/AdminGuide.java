package studio.golden.adminapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AdminGuide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_guide);
        getSupportActionBar().setTitle("Admin Guide");
    }
}
