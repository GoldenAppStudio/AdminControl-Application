package studio.golden.adminapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class StudentsNotification extends AppCompatActivity {

    ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_notification);

        getSupportActionBar().setTitle("Notifications");

        mListView = findViewById(R.id.notification_listView);

        final String[] Name =  {"Notification For Student","Notification For Class","Notification For School"};

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Name);


        mListView.setAdapter(adapter);



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String myNames = Name[position];
                if(myNames == Name[0])
                {
                    Intent intent =  new Intent(StudentsNotification.this,NewStudentRegister.class);
                    startActivity(intent);

                    Toast.makeText(StudentsNotification.this, "" + myNames, Toast.LENGTH_SHORT).show();
                }
                else if (myNames == Name[1]){
                    Intent intent =  new Intent(StudentsNotification.this,ChangeStudentData.class);
                    startActivity(intent);

                    Toast.makeText(StudentsNotification.this, "" + myNames, Toast.LENGTH_SHORT).show();
                }
                else if (myNames == Name[1]){
                    Intent intent =  new Intent(StudentsNotification.this,ChangeStudentData.class);
                    startActivity(intent);

                    Toast.makeText(StudentsNotification.this, "" + myNames, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
