package comq.example.raymond.crimereport2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.crimereport2.Interface.ItemClickListener;
import comq.example.raymond.crimereport2.Model.ReportModel;
import comq.example.raymond.crimereport2.Utils.ReportUtils;

public class CrimeReported extends AppCompatActivity {

    private DatabaseReference mDatabaseCrimes;
    private FirebaseAuth mAuth;
    private String uId = "";


    private FirebaseRecyclerAdapter<ReportModel, CrimesReportedViewHolder> adapter;
    private RecyclerView recyclerView_reporter_crimes_reported;
    RecyclerView.LayoutManager layoutManager;



    //toolbar
    private android.support.v7.widget.Toolbar crime_reported;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_reported);

        mDatabaseCrimes = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("crimesReported");
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        //initialize toolBar
        crime_reported = findViewById(R.id.crime_reported_toolbar);
        setSupportActionBar(crime_reported);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Crimes you reported");

        recyclerView_reporter_crimes_reported = findViewById(R.id.recycler_reporter_crimes_reported);
        recyclerView_reporter_crimes_reported.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_reporter_crimes_reported.setLayoutManager(layoutManager);


        loadCrimesReported();


    }

    private void loadCrimesReported() {
        FirebaseRecyclerOptions<ReportModel> options = new FirebaseRecyclerOptions.Builder<ReportModel>()
                .setQuery(mDatabaseCrimes.orderByChild("reporterId").equalTo(uId), ReportModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ReportModel, CrimesReportedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CrimesReportedViewHolder holder, int position, @NonNull ReportModel model) {
                holder.txtCrimeType.setText(model.getCrimeType());
                holder.txtDateReported.setText(ReportUtils.dateFromLong(model.getReportDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get crime id to new activity
                        Intent reporterDetail = new Intent(CrimeReported.this, CrimeReportedDetail.class);
                        reporterDetail.putExtra("crimeId", adapter.getRef(position).getKey());
                        startActivity(reporterDetail);
                    }
                });
            }

            @NonNull
            @Override
            public CrimesReportedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reporter_crime_reported_layout, viewGroup,false);
                CrimesReportedViewHolder viewHolder = new CrimesReportedViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView_reporter_crimes_reported.setAdapter(adapter);
        adapter.startListening();
    }

    public static class CrimesReportedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCrimeType, txtDateReported;
        private ItemClickListener itemClickListener;
        public CrimesReportedViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCrimeType = itemView.findViewById(R.id.crime_type);
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
