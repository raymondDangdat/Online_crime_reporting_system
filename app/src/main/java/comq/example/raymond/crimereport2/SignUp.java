package comq.example.raymond.crimereport2;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {
    private CircleImageView profilePic;
    private EditText editTextName, editTextPassword, editTextCPassword,editTextEmail,
    editTextOccupation, editTextAddress, editTextPhone;
    private ImageView imgId;
    private Spinner spinnerLg;
    private TextView txtSignInOption;
    private Button btnUploadId, btnSignUp;


    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int ID_REQUEST_CODE = 101;
    private Uri mImageUri = null;

    ProgressDialog mProgress;


    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    //toolbar
    private android.support.v7.widget.Toolbar signup_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mProgress = new ProgressDialog(this);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("reporters");
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("profilePix");




        //initialize toolBar
        signup_toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(signup_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign Up");




        //initializing views
        profilePic = findViewById(R.id.setUpImageButton);
        editTextName =findViewById(R.id.edt_fullname);
        editTextCPassword = findViewById(R.id.edt_c_password);
        editTextPassword = findViewById(R.id.edt_password);
        editTextEmail = findViewById(R.id.edt_email);
        editTextAddress = findViewById(R.id.edt_address);
        editTextPhone = findViewById(R.id.edt_phone);
        editTextOccupation = findViewById(R.id.edt_occupation);
        spinnerLg = findViewById(R.id.spinner_lg);
        //imgId = findViewById(R.id.img_id);
        txtSignInOption = findViewById(R.id.txt_sign_in_option);
        btnSignUp = findViewById(R.id.btn_sign_up);
        //btnUploadId = findViewById(R.id.btn_upload_id);



        //set onclick listeners
        txtSignInOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerReporter();
            }
        });
        
//        btnUploadId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadId();
//            }
//        });
        
        
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePic();
            }
        });

//        imgId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent, "Select Image"),
//                        ID_REQUEST_CODE);
//
//            }
//        });

    }

    private void registerReporter() {
        mProgress.setMessage("Registering...");
        final String fullName = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String lg = spinnerLg.getSelectedItem().toString();
        final String address = editTextAddress.getText().toString().trim();
        final String occupation =editTextOccupation.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        String location = editTextOccupation.getText().toString();
        String password = editTextPassword.getText().toString().trim();
        String cPassword = editTextCPassword.getText().toString().trim();

        //check for emptiness
        if (!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(lg)
                && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(occupation) && !TextUtils.isEmpty(phone)
                && !TextUtils.isEmpty(location) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(cPassword)  && mImageUri != null){
            if (password.equals(cPassword)){
                mProgress.show();
                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
                        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final String user_id = mAuth.getCurrentUser().getUid();
                                String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                                mDatabaseUsers.child(user_id).child("name").setValue(fullName);
                                mDatabaseUsers.child(user_id).child("email").setValue(email);
                                mDatabaseUsers.child(user_id).child("lg").setValue(lg);
                                mDatabaseUsers.child(user_id).child("address").setValue(address);
                                mDatabaseUsers.child(user_id).child("profilePic").setValue(downloadUrl);
                                mDatabaseUsers.child(user_id).child("Occupation").setValue(occupation);
                                mDatabaseUsers.child(user_id).child("phone").setValue(phone)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    mProgress.dismiss();
                                                    Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                                    Intent loginIntent = new Intent(SignUp.this, UploadId.class);
                                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(loginIntent);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.dismiss();
                        Toast.makeText(SignUp.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }else{
                Toast.makeText(this, "Password does not match confirm password", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "You can't sign in with empty fields", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadProfilePic() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
//            Uri imageUri = data.getData();
//            if (imageUri != null){
//                profilePic.setImageURI(imageUri);
//            }
//
//        }else if (requestCode == ID_REQUEST_CODE && resultCode == RESULT_OK){
//            ClipData clipData = data.getClipData();
//            if (clipData != null){
//                profilePic.setImageURI(clipData.getItemAt(0).getUri());
//                imgId.setImageURI(clipData.getItemAt(1).getUri());
//
//                for (int i = 0; i < clipData.getItemCount(); i++){
//                    ClipData.Item item = clipData.getItemAt(i);
//                    Uri uri = item.getUri();
//
//                    Log.e("Multiple images", uri.toString());
//                }
//            }
//        }
//    }

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
                profilePic.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void uploadId() {
    }
}
