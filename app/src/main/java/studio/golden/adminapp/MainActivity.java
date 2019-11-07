package studio.golden.adminapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent intent=new Intent(Intent.ACTION_SEND);
    String[] recipients={"studiogoldenapp@gmail.com"};

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Server Updated Successfully...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mListView = findViewById(R.id.listView);

        final String[] Name =  {"Register New Student ","Edit Student Data","All Notifications","Student's Attendance","School Facilities"};

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Name);


        mListView.setAdapter(adapter);



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String myNames = Name[position];
                if(myNames == Name[0])
                {
                    Intent intent =  new Intent(MainActivity.this,NewStudentRegister.class);
                    startActivity(intent);

                    Toast.makeText(MainActivity.this, "" + myNames, Toast.LENGTH_SHORT).show();
                }
                else if (myNames == Name[1]){
                    Intent intent =  new Intent(MainActivity.this,ChangeStudentData.class);
                    startActivity(intent);

                    Toast.makeText(MainActivity.this, "" + myNames, Toast.LENGTH_SHORT).show();
                }
                else if (myNames == Name[2])
                {
                    Intent intent =  new Intent(MainActivity.this,StudentsNotification.class);
                    startActivity(intent);

                    Toast.makeText(MainActivity.this, "" + myNames, Toast.LENGTH_SHORT).show();
                }
                else if(myNames == Name[3])
                {
                    Intent intent =  new Intent(MainActivity.this,Attendance.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "" + myNames, Toast.LENGTH_SHORT).show();

                }
                else if (myNames == Name[4])
                {
                    Intent intent =  new Intent(MainActivity.this,Facilities.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "" + myNames, Toast.LENGTH_SHORT).show();

                }



            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                        quit();
                    }
                }).create().show();
    }
    public void quit() {
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivities(new Intent[]{intent});
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_attendance) {
            Intent intent = new Intent(MainActivity.this,Attendance.class);
            startActivities(new Intent[]{intent});

            // Handle the camera action
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(MainActivity.this,StudentsNotification.class);
            startActivities(new Intent[]{intent});

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivities(new Intent[]{intent});

        }else if (id == R.id.nav_imp_note) {
            Intent intent = new Intent(MainActivity.this,AdminGuide.class);
            startActivities(new Intent[]{intent});

        }
        else if (id == R.id.nav_exit) {
            onBackPressed();

        } else if (id == R.id.nav_report_bug) {

            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Title here...");
            intent.putExtra(Intent.EXTRA_TEXT,"Body of the content here...");
            intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send mail"));

        } else if (id == R.id.nav_developer) {

            Uri developer_uri = Uri.parse("https://hw-faction.github.io/goldenappstudio/");
            Intent intent = new Intent(Intent.ACTION_VIEW, developer_uri);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
