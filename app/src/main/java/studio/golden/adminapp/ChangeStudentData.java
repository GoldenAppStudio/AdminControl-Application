package studio.golden.adminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChangeStudentData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_student_data);

        getSupportActionBar().setTitle("Edit Student Data");
    }
}