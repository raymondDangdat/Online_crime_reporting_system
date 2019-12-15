package comq.example.raymond.crimereport2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

import comq.example.raymond.crimereport2.Interface.ItemClickListener;
import comq.example.raymond.crimereport2.Model.FeedbackModel;
import comq.example.raymond.crimereport2.Model.ReportModel;
import comq.example.raymond.crimereport2.Utils.ReportUtils;

public class ACrimeReportedDetail extends AppCompatActivity {
    //toolbar
    private android.support.v7.widget.Toolbar crime_reported_detail;

    private String crimeId = "";

    private Button btnSend;
    private TextView txtCrimeType, txtDateReported, txtCrimeDescription, txtReporterPhone;
    private EditText editTextFeedback;
    private ImageView imgCrimeScene;

    private RecyclerView recyclerView_feedback;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<FeedbackModel, FeedbacktedViewHolder>adapter;

    private FeedbackModel feedbackModel;



    private DatabaseReference mDatabaseUsers, mDatabaseCrimes, mDatabaseFeedbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acrime_reported_detail);

        mDatabaseCrimes = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("crimesReported");
        mDatabaseFeedbacks = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("crimesReported").child("feedbacks");
        //mDatabaseFeedbacks = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("feedbacks");



        //initialize toolBar
        crime_reported_detail = findViewById(R.id.crime_reported_detail_toolbar);
        setSupportActionBar(crime_reported_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Details of crime");

        recyclerView_feedback = findViewById(R.id.crime_progress);
        recyclerView_feedback.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_feedback.setLayoutManager(layoutManager);


        btnSend = findViewById(R.id.btn_send);
        txtCrimeType = findViewById(R.id.crime_type);
        txtDateReported = findViewById(R.id.txt_date);
        txtCrimeDescription = findViewById(R.id.crime_description);
        txtReporterPhone = findViewById(R.id.crime_reporter_phone);
        editTextFeedback = findViewById(R.id.edt_feedback);
        imgCrimeScene = findViewById(R.id.img_crime_scene);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFeedback();
            }
        });




        //get crime id from Intent
        if (getIntent() != null){
            crimeId = getIntent().getStringExtra("crimeId");

            if (!crimeId.isEmpty()){
                getCrimeDetail(crimeId);
            }

        }



        loadFeedback(crimeId);
    }

    private void loadFeedback(String crimeId) {
        FirebaseRecyclerOptions<FeedbackModel>options = new FirebaseRecyclerOptions.Builder<FeedbackModel>()
                .setQuery(mDatabaseFeedbacks.orderByChild("crimeId").equalTo(crimeId), FeedbackModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<FeedbackModel, FeedbacktedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FeedbacktedViewHolder holder, int position, @NonNull FeedbackModel model) {
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
            public FeedbacktedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feedback_layout, viewGroup,false);
                FeedbacktedViewHolder viewHolder = new FeedbacktedViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView_feedback.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateFeedback() {
        String feedBack = editTextFeedback.getText().toString().trim();
        final long dateGiven = new Date().getTime();
        if (!TextUtils.isEmpty(feedBack)){
            feedbackModel = new FeedbackModel(feedBack, crimeId, dateGiven);
            mDatabaseFeedbacks.push().setValue(feedbackModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ACrimeReportedDetail.this, "Feedback sent!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ACrimeReportedDetail.this, AdminHome.class));
                    }
                }
            });
        }else Toast.makeText(this, "Feedback cannot be empty", Toast.LENGTH_SHORT).show();
    }


    private void getCrimeDetail(String crimeId) {
        mDatabaseCrimes.child(crimeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ReportModel reportModel = dataSnapshot.getValue(ReportModel.class);
                txtCrimeType.setText("Crime Type: "+ reportModel.getCrimeType());
                txtDateReported.setText("Date Reported: "+ ReportUtils.dateFromLong(reportModel.getReportDate()));
                txtCrimeDescription.setText("Crime Description: "+ reportModel.getCrimeDescription());
                txtReporterPhone.setText("Call Reporter: " + reportModel.getReporterPhone());
                String img_url = reportModel.getCrimeScene().toString();

                //make call
                final String phone = reportModel.getReporterPhone().toString();
                txtReporterPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                        phoneIntent.setData(Uri.parse("tel:"+phone));
                        if (ActivityCompat.checkSelfPermission(ACrimeReportedDetail.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(phoneIntent);
                    }
                });

                if (img_url.equals("NULL")){

                }else {
                    imgCrimeScene.setVisibility(View.VISIBLE);
                    Picasso.get().load(reportModel.getCrimeScene()).into(imgCrimeScene);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static class FeedbacktedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtFeedback, txtDate;
        private ItemClickListener itemClickListener;
        public FeedbacktedViewHolder(@NonNull View itemView) {
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
