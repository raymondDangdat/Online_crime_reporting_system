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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.crimereport2.Interface.ItemClickListener;
import comq.example.raymond.crimereport2.Model.ApproveModel;
import comq.example.raymond.crimereport2.Model.ReportModel;

public class ApprovedReporters extends AppCompatActivity {


    //toolbar
    private android.support.v7.widget.Toolbar approved_reporters_toolbar;
    private DatabaseReference mDatabaseUsers;

    private FirebaseRecyclerAdapter<ApproveModel, ApprovedReportersViewHolder>adapter;
    private RecyclerView recyclerView_approved_reporters;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_reporters);


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("reporters");


        recyclerView_approved_reporters = findViewById(R.id.recycler_approvedReporters);
        recyclerView_approved_reporters.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_approved_reporters.setLayoutManager(layoutManager);




        //initialize toolBar
        approved_reporters_toolbar = findViewById(R.id.approved_reporters_toolbar);
        setSupportActionBar(approved_reporters_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Approved Reporters");


        loadApprovedReporters();



    }

    private void loadApprovedReporters() {
        FirebaseRecyclerOptions<ApproveModel>options = new FirebaseRecyclerOptions.Builder<ApproveModel>()
                .setQuery(mDatabaseUsers.orderByChild("status").equalTo("YES"), ApproveModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ApproveModel, ApprovedReportersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ApprovedReportersViewHolder holder, int position, @NonNull ApproveModel model) {
                holder.txtLg.setText("LGA: " + model.getLg());
                holder.txtPhone.setText("Phone Number: " + model.getPhone());
                holder.txtname.setText("Full Name: " + model.getName());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get reporter id to new activity
                        Intent reporterDetail = new Intent(ApprovedReporters.this, ApproveReporterDetails.class);
                        reporterDetail.putExtra("reporterId", adapter.getRef(position).getKey());
                        startActivity(reporterDetail);
                    }
                });
            }

            @NonNull
            @Override
            public ApprovedReportersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approved_reporters_layout, viewGroup,false);
                ApprovedReportersViewHolder viewHolder = new ApprovedReportersViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView_approved_reporters.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ApprovedReportersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtname, txtLg, txtPhone;
        private ItemClickListener itemClickListener;
        public ApprovedReportersViewHolder(@NonNull View itemView) {
            super(itemView);

            txtname = itemView.findViewById(R.id.reporter_name);
            txtLg = itemView.findViewById(R.id.reporter_lg);
            txtPhone = itemView.findViewById(R.id.phone);

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
