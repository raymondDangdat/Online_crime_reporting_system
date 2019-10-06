package comq.example.raymond.crimereport2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReporterHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    private TextView txtFullname;
    private CircleImageView profilePic;

    private String uId = "";
    private DatabaseReference mDatabaseUsers, mDatabaseCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporter_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("reporters");
        mDatabaseCrimes = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("crimesReported");


        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                View headerView = navigationView.getHeaderView(0);
                txtFullname = headerView.findViewById(R.id.txt_fullname);
                profilePic = headerView.findViewById(R.id.profile_pix);

                String fullname = dataSnapshot.child(uId).child("name").getValue(String.class);
                String profilePicture = dataSnapshot.child(uId).child("profilePic").getValue(String.class);

                //set text
                txtFullname.setText(fullname);
                Picasso.get().load(profilePicture).into(profilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reporter_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_report_crime) {
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String uStatus = dataSnapshot.child(uId).child("status").getValue(String.class);

                    if (uStatus.equals("YES")){
                        startActivity(new Intent(ReporterHome.this, ReportCrime.class));
                    }else if (uStatus.equals("NO")){
                        Toast.makeText(ReporterHome.this, "Sorry you application is yet to be approved!", Toast.LENGTH_LONG).show();
                    }else if (uStatus.equals("DECLINED")){
                        Toast.makeText(ReporterHome.this, "Sorry, you can't report crime because your information are incorrect", Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (id == R.id.nav_track_crime_reported) {
            startActivity(new Intent(ReporterHome.this, CrimeReported.class));

        } else if (id == R.id.nav_latest_crimes) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_sign_out) {
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(ReporterHome.this, SignIn.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();

        }else if (id == R.id.nav_camera_capture){
            startActivity(new Intent(ReporterHome.this, CameraCapture.class));
        }else if (id == R.id.nav_police_emc){
            startActivity(new Intent(ReporterHome.this, PoliceEmergencyContacts.class));
        }
//        else if (id == R.id.nav_upload_id){
//            startActivity(new Intent(ReporterHome.this, UploadId.class));
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
