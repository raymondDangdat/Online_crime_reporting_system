package comq.example.raymond.crimereport2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import comq.example.raymond.crimereport2.Interface.ItemClickListener;
import comq.example.raymond.crimereport2.Model.ReportModel;
import comq.example.raymond.crimereport2.Utils.ReportUtils;

public class AdminHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseCrimes;
    //add new chalet layout
    private MaterialEditText editTextChaletNumber;

    private FirebaseRecyclerAdapter<ReportModel, CrimesReportedViewHolder> adapter;
    private RecyclerView recyclerView_reporter_crimes_reported;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseCrimes = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("crimesReported");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView_reporter_crimes_reported = findViewById(R.id.crimes_reported);
        recyclerView_reporter_crimes_reported.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_reporter_crimes_reported.setLayoutManager(layoutManager);


        loadCrimesReported();


    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminHome.this);
        alertDialog.setTitle("Add Crime");
        alertDialog.setMessage("Please fill the information correctly");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_chalet_layout = inflater.inflate(R.layout.add_crime_layout, null);

        editTextChaletNumber = add_chalet_layout.findViewById(R.id.edtChaletNumber);
        // btnSelect = add_chalet_layout.findViewById(R.id.btnSelect);
        //btnUpload = add_chalet_layout.findViewById(R.id.btnUpload);

        alertDialog.setView(add_chalet_layout);
        //alertDialog.setIcon(R.drawable.ic_home_black_24dp);


        //set button
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();



            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCrimesReported();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCrimesReported();
    }

    private void loadCrimesReported() {
        FirebaseRecyclerOptions<ReportModel>options = new FirebaseRecyclerOptions.Builder<ReportModel>()
                .setQuery(mDatabaseCrimes, ReportModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ReportModel, CrimesReportedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CrimesReportedViewHolder holder, int position, @NonNull ReportModel model) {
                holder.txtReporterPhone.setText(model.getReporterPhone());
                holder.txtCrimeLocation.setText(model.getCrimeLocation());
                holder.txtCrimeType.setText(model.getCrimeType());
                holder.txtDateReported.setText(ReportUtils.dateFromLong(model.getReportDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get crime id to new activity
                        Intent reporterDetail = new Intent(AdminHome.this, ACrimeReportedDetail.class);
                        reporterDetail.putExtra("crimeId", adapter.getRef(position).getKey());
                        startActivity(reporterDetail);


                    }
                });
            }

            @NonNull
            @Override
            public CrimesReportedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.crime_reported_to_admin, viewGroup,false);
                CrimesReportedViewHolder viewHolder = new CrimesReportedViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView_reporter_crimes_reported.setAdapter(adapter);
        adapter.startListening();
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
        getMenuInflater().inflate(R.menu.admin_home, menu);
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

        if (id == R.id.nav_approve_reporter) {
            // Handle the camera action
            startActivity(new Intent(AdminHome.this, ApproveReporter.class));
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_sign_out) {
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(AdminHome.this, SignIn.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();


        }else if (id == R.id.nav_approved_reporters){
            startActivity(new Intent(AdminHome.this, ApprovedReporters.class));
        }else if (id == R.id.nav_crimes_reported){
            startActivity(new Intent(AdminHome.this, AdminCrimesReported.class));
      }
        else if(id == R.id.nav_add_emergency_number){
            startActivity(new Intent(AdminHome.this, ManageEmergencyContacts.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class CrimesReportedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCrimeType, txtCrimeLocation, txtReporterPhone, txtDateReported;
        private ItemClickListener itemClickListener;
        public CrimesReportedViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCrimeType = itemView.findViewById(R.id.crime_type);
            txtCrimeLocation = itemView.findViewById(R.id.crime_location);
            txtReporterPhone = itemView.findViewById(R.id.reporter_phone);
            txtDateReported = itemView.findViewById(R.id.txt_date);

            itemView.setOnClickListener(this);
        }


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
