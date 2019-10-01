package comq.example.raymond.crimereport2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

import comq.example.raymond.crimereport2.Model.ReportModel;

public class ReportCrime extends AppCompatActivity {

    //toolbar
    private android.support.v7.widget.Toolbar report_crime;

    private Button btnUploadScene, btnSubmit;
    private Spinner spinnerCrimeType;
    private EditText editTextCrimeDescription, crimeLocation;
    private ImageView img_crime_scene;

    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri = null;


    private DatabaseReference mDatabaseUsers, mDatabaseCrimes;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;
    private String uId = "";

    private ProgressDialog reportProgress;

    private ReportModel reportModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_crime);

        reportProgress = new ProgressDialog(this);


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("reporters");
        mDatabaseCrimes = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("crimesReported");
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("crime_scenes");


        btnSubmit = findViewById(R.id.btn_submit);
        btnUploadScene = findViewById(R.id.btn_upload_crime_scene);
        spinnerCrimeType = findViewById(R.id.spinner_crime_type);
        editTextCrimeDescription = findViewById(R.id.crime_description);
        img_crime_scene = findViewById(R.id.img_crime_scene);
        crimeLocation = findViewById(R.id.crime_location);






        //initialize toolBar
        report_crime = findViewById(R.id.report_crime);
        setSupportActionBar(report_crime);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Report Crime");

        btnUploadScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadScene();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
    }

    private void submitReport() {
        final String crime_description = editTextCrimeDescription.getText().toString().trim();
        final String crime_location = crimeLocation.getText().toString().trim();
        final String crime_type = spinnerCrimeType.getSelectedItem().toString();

        final String reporterId = mAuth.getCurrentUser().getUid();

        final long reportDate = new Date().getTime();

        if (TextUtils.isEmpty(crime_description)){
            Toast.makeText(this, "Please give a description of the crime! ", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(crime_location)){
            Toast.makeText(this, "Crime location cannot be empty", Toast.LENGTH_SHORT).show();

        }else if (crime_type.equals("Click to select crime type")){
            Toast.makeText(this, "Please select a valid crime type", Toast.LENGTH_SHORT).show();
        }else {
            reportProgress.setMessage("Processing report...");
            reportProgress.show();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String reporterName = dataSnapshot.child(uId).child("name").getValue(String.class);
                    final String reporterPhone = dataSnapshot.child(uId).child("phone").getValue(String.class);
                    final String reporterProfile = dataSnapshot.child(uId).child("profilePic").getValue(String.class);


                    if (mImageUri != null){
                        StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
                        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String crimeScene = taskSnapshot.getDownloadUrl().toString();
                                reportProgress.setMessage("Sending report...");
                                reportModel = new ReportModel(crime_type, crime_description, crime_location,
                                        crimeScene,reporterId, reporterName,reporterPhone, reporterProfile, reportDate);
                                mDatabaseCrimes.push().setValue(reportModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            reportProgress.dismiss();
                                            Toast.makeText(ReportCrime.this, "Report Sent", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ReportCrime.this, ReporterHome.class));
                                        }
                                    }
                                });

                            }
                        });
                    }else {
                        reportProgress.setMessage("Sending report...");
                        String crimeScene = "NULL";
                        reportProgress.setMessage("Sending report...");
                        reportModel = new ReportModel(crime_type, crime_description, crime_location,
                                crimeScene,reporterId, reporterName,reporterPhone, reporterProfile, reportDate);
                        mDatabaseCrimes.push().setValue(reportModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    reportProgress.dismiss();
                                    Toast.makeText(ReportCrime.this, "Report Sent", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ReportCrime.this, ReporterHome.class));

                                }
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


    private void uploadScene() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //to set it to square
                    .setAspectRatio(1,2)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                img_crime_scene.setVisibility(View.VISIBLE);
                img_crime_scene.setImageURI(mImageUri);
                btnUploadScene.setText("Change Image");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
