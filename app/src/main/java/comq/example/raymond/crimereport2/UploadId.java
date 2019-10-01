package comq.example.raymond.crimereport2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class UploadId extends AppCompatActivity {
    //toolbar
    private android.support.v7.widget.Toolbar upload_id_toolbar;

    private Button btn_upload;
    private ImageView img_id;

    private ProgressDialog upload_progress;

    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri = null;

    String uId = "";


    private DatabaseReference mDatabaseUsers, mDatabaseIds;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_id);

        btn_upload = findViewById(R.id.btn_upload_id);
        img_id = findViewById(R.id.img_id);

        upload_progress = new ProgressDialog(this);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("reporters");
        mDatabaseIds = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("ids");
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("ids");

        uId = mAuth.getCurrentUser().getUid();

        //initialize toolBar
        upload_id_toolbar = findViewById(R.id.upload_id_toolbar);
        setSupportActionBar(upload_id_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Upload Your Id");

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadId();
            }
        });


        img_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_id();
            }
        });

    }

    private void upload_id() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //to set it to square
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                img_id.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void uploadId() {
        mDatabaseUsers.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("img_id").exists()){
                    Toast.makeText(UploadId.this, "Your Id have been uploaded already", Toast.LENGTH_SHORT).show();
                }else{
                    if (mImageUri != null){
                        upload_progress.setMessage("Uploading...");
                        upload_progress.show();


                                    StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
                                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                            mDatabaseUsers.child(uId).child("img_id").setValue(downloadUrl);
                                            mDatabaseUsers.child(uId).child("status").setValue("NO")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                upload_progress.dismiss();
                                                                Toast.makeText(UploadId.this, "Id Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                                Intent loginIntent = new Intent(UploadId.this, SignIn.class);
                                                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(loginIntent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }
                                    });



                    }else {
                        Toast.makeText(UploadId.this, "Please click to pick your Id from your gallery", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
