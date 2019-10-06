package comq.example.raymond.crimereport2;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import comq.example.raymond.crimereport2.Model.ApproveModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class ApproveReporterDetails extends AppCompatActivity {

    //toolbar
    private android.support.v7.widget.Toolbar approve_reporter_detail_toolbar;

    private DatabaseReference mDatabaseUsers;

    private CircleImageView imgProfilePic;
    private TextView txtName, txtLg, txtAddress, txtOccupation, txtPhone, txtEmail;
    private ImageView img_id;
    private Button btnAccept, btnDecline;

    private String reporterId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_reporter_details);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("reporters");


        //initialize toolBar
        approve_reporter_detail_toolbar = findViewById(R.id.approve_reporter_detail_toolbar);
        setSupportActionBar(approve_reporter_detail_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reporter Details");

        btnAccept = findViewById(R.id.btn_approve);
        btnDecline = findViewById(R.id.btn_decline);
        imgProfilePic = findViewById(R.id.profile_image);
        img_id = findViewById(R.id.reporter_id);
        txtAddress = findViewById(R.id.txt_address);
        txtName = findViewById(R.id.txt_fullname);
        txtLg = findViewById(R.id.txt_lg);
        txtOccupation = findViewById(R.id.txt_occupation);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);



        //get room id from Intent
        if (getIntent() != null){
            reporterId = getIntent().getStringExtra("reporterId");

            if (!reporterId.isEmpty()){
                getReporterDetail(reporterId);
            }

        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveReporter();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineReporter();
            }
        });
    }

    private void declineReporter() {
        mDatabaseUsers.child(reporterId).child("status").setValue("DECLINED")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ApproveReporterDetails.this, "Reporter Declined", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ApproveReporterDetails.this, ApproveReporter.class));
                        }
                    }
                });
    }

    private void approveReporter() {
        mDatabaseUsers.child(reporterId).child("status").setValue("YES")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ApproveReporterDetails.this, "Approved Succesfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ApproveReporterDetails.this, ApproveReporter.class));
                        }
                    }
                });
    }

    private void getReporterDetail(String reporterId) {
        mDatabaseUsers.child(reporterId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ApproveModel approveModel = dataSnapshot.getValue(ApproveModel.class);
                txtAddress.setText("Address: " + approveModel.getAddress());
                txtLg.setText("LGA: " + approveModel.getLg());
                txtName.setText("Name: "+ approveModel.getName());
                Picasso.get().load(approveModel.getImg_id()).into(img_id);
                Picasso.get().load(approveModel.getProfilePic()).into(imgProfilePic);
                String status = approveModel.getStatus().toString();
                txtEmail.setText("Email: " + approveModel.getEmail());
                txtPhone.setText("Phone Number: " + approveModel.getPhone());
                txtOccupation.setText("Occupation: " + approveModel.getOccupation());
                if (status.equals("YES")){
                    btnDecline.setText("REMOVE");
                    btnAccept.setText("Approved");
                    btnAccept.setEnabled(false);
                    btnDecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            declineReporter();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
