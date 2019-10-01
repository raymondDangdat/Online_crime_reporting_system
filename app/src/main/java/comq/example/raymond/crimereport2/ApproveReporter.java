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
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.crimereport2.Interface.ItemClickListener;
import comq.example.raymond.crimereport2.Model.ApproveModel;

public class ApproveReporter extends AppCompatActivity {
//button for testing
    //private Button btnNext;

    private DatabaseReference mDatabaseUsers;






    private FirebaseRecyclerAdapter<ApproveModel, ApproveReporterViewHolder> adapter;
    private RecyclerView recyclerView_unapproved_reporters;
    RecyclerView.LayoutManager layoutManager;

    //toolbar
    private android.support.v7.widget.Toolbar approve_reporter_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_reporter);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("reporters");


        //initialize toolBar
        approve_reporter_toolbar = findViewById(R.id.approve_reporter_toolbar);
        setSupportActionBar(approve_reporter_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Unapproved Reporters");



        recyclerView_unapproved_reporters = findViewById(R.id.recycler_unapprovedReporters);
        recyclerView_unapproved_reporters.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_unapproved_reporters.setLayoutManager(layoutManager);

//
//        btnNext = findViewById(R.id.next);
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ApproveReporter.this, ApproveReporterDetails.class));
//            }
//        });
        
        loadUnApprovedReporters();
    }

    private void loadUnApprovedReporters() {
        FirebaseRecyclerOptions<ApproveModel> options = new FirebaseRecyclerOptions.Builder<ApproveModel>()
                .setQuery(mDatabaseUsers.orderByChild("status").equalTo("NO"), ApproveModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ApproveModel, ApproveReporterViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ApproveReporterViewHolder holder, int position, @NonNull ApproveModel model) {
                holder.txtLg.setText(model.getLg());
                holder.txtname.setText(model.getName());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get reporter id to new activity
                        Intent reporterDetail = new Intent(ApproveReporter.this, ApproveReporterDetails.class);
                        reporterDetail.putExtra("reporterId", adapter.getRef(position).getKey());
                        startActivity(reporterDetail);
                    }
                });
            }

            @NonNull
            @Override
            public ApproveReporterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.unapproved_reporters_layout, viewGroup,false);
                ApproveReporterViewHolder viewHolder = new ApproveReporterViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView_unapproved_reporters.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ApproveReporterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtname, txtLg;
        private ItemClickListener itemClickListener;
        public ApproveReporterViewHolder(@NonNull View itemView) {
            super(itemView);

            txtname = itemView.findViewById(R.id.reporter_name);
            txtLg = itemView.findViewById(R.id.reporter_lg);

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
