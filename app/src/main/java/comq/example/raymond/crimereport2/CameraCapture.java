package comq.example.raymond.crimereport2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CameraCapture extends AppCompatActivity {
    private Button btnCamera;
    private ImageView imgCaptured;

    private static int CAMERA_REQUEST_CODE = 1;

    private StorageReference mStorageImage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        btnCamera = findViewById(R.id.btn_camera);
        imgCaptured = findViewById(R.id.image_captured);

        progressDialog = new ProgressDialog(this);

        mStorageImage = FirebaseStorage.getInstance().getReference().child("crime_scenes");


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureIamge();
            }
        });
    }

    private void captureIamge() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode ==RESULT_OK){
            progressDialog.setMessage("Uploading image...");
            progressDialog.show();
            Uri uri = data.getData();

            StorageReference filePath = mStorageImage.child("photos").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(CameraCapture.this, "Uploaded successfully! ", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CameraCapture.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
