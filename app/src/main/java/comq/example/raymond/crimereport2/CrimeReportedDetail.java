package comq.example.raymond.crimereport2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import comq.example.raymond.crimereport2.Interface.ItemClickListener;
import comq.example.raymond.crimereport2.Model.FeedbackModel;
import comq.example.raymond.crimereport2.Model.ReportModel;
import comq.example.raymond.crimereport2.Utils.ReportUtils;

public class CrimeReportedDetail extends AppCompatActivity {


    //toolbar
    private android.support.v7.widget.Toolbar crime_reported_detail;


    private String crimeId = "";

    private TextView txtCrimeType, txtDateReported;

    private RecyclerView recyclerView_feedback;
    RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton fab;

    private FirebaseRecyclerAdapter<FeedbackModel, FeedbackViewHolder>adapter;



    private DatabaseReference mDatabaseUsers, mDatabaseCrimes, mDatabaseFeedbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_reported_detail);


        //initialize toolBar
        crime_reported_detail = findViewById(R.id.crime_reported_detail_toolbar);
        setSupportActionBar(crime_reported_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Details of crime you reported");

        mDatabaseCrimes = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("crimesReported");
        //mDatabaseFeedbacks = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("crimesReported").child("feedbacks");
        mDatabaseFeedbacks = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("feedbacks");

        recyclerView_feedback = findViewById(R.id.crime_progress);
        recyclerView_feedback.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_feedback.setLayoutManager(layoutManager);


        txtCrimeType = findViewById(R.id.crime_type);
        txtDateReported = findViewById(R.id.txt_date);

        fab = findViewById(R.id.fab);



        //get room id from Intent
        if (getIntent() != null){
            crimeId = getIntent().getStringExtra("crimeId");

            if (!crimeId.isEmpty()){
                getCrimeDetail(crimeId);
            }

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  loadFeedback(crimeId);
            }
        });


        loadFeedback(crimeId);

    }

    private void loadFeedback(String crimeId) {
        FirebaseRecyclerOptions<FeedbackModel>options = new FirebaseRecyclerOptions.Builder<FeedbackModel>()
                .setQuery(mDatabaseFeedbacks.orderByChild("crimeId").equalTo(crimeId), FeedbackModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<FeedbackModel, FeedbackViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position, @NonNull FeedbackModel model) {
                holder.txtFeedback.setText(model.getFeedback());
                holder.txtDate.setText(ReportUtils.dateFromLong(model.getDateGiven()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feedback_layout, viewGroup,false);
                FeedbackViewHolder viewHolder = new FeedbackViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView_feedback.setAdapter(adapter);
        adapter.startListening();
    }

    private void getCrimeDetail(String crimeId) {
        mDatabaseCrimes.child(crimeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ReportModel reportModel = dataSnapshot.getValue(ReportModel.class);
                txtCrimeType.setText("Crime Type: "+ reportModel.getCrimeType());
                txtDateReported.setText("Date Reported: "+ ReportUtils.dateFromLong(reportModel.getReportDate()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public static class FeedbackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtFeedback, txtDate;
        private ItemClickListener itemClickListener;
        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFeedback = itemView.findViewById(R.id.feedback);
            txtDate = itemView.findViewById(R.id.txt_date);

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
