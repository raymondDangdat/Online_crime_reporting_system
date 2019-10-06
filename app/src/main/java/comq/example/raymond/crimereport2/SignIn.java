package comq.example.raymond.crimereport2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
    private EditText editText_email;
    private EditText editText_password;
    private Button button_sing_in;
    private TextView txt_sign_up_option, txt_forgot_password;

    private ProgressDialog loginProgress;

    private FirebaseAuth mAuth;

    //toolbar
    private android.support.v7.widget.Toolbar signin_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        loginProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();



        //initialize toolBar
        signin_toolbar = findViewById(R.id.main_signin_toolbar);
        setSupportActionBar(signin_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign In");


        editText_email = findViewById(R.id.edt_email);
        editText_password = findViewById(R.id.edt_password);
        button_sing_in = findViewById(R.id.btn_sign_in);
        txt_sign_up_option = findViewById(R.id.txt_sign_up_option);
        txt_forgot_password = findViewById(R.id.txt_forgotten_password);



        txt_sign_up_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
        button_sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });
        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, ForgotPassword.class));
            }
        });
    }

    private void loginUser() {
        final String email = editText_email.getText().toString().trim();
        String password = editText_password.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            loginProgress.setMessage("Login in...");
            loginProgress.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            loginProgress.dismiss();
                            if (email.equals("ocrsadmin@gmail.com")){
                                Intent loginIntent = new Intent(SignIn.this, AdminHome.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            }else{
                                Intent loginIntent = new Intent(SignIn.this, ReporterHome.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loginProgress.dismiss();
                    Toast.makeText(SignIn.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}
