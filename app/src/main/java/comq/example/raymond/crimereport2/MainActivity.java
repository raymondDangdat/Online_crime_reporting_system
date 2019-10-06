package comq.example.raymond.crimereport2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnSignUp;
    private Button btnSignIn;
    private Button btnEmergencyContacts;

    //toolbar
    private android.support.v7.widget.Toolbar main_activity_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initialize toolBar
        main_activity_toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(main_activity_toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Crime Reporting System");

        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnEmergencyContacts = findViewById(R.id.btn_emergency_numbers);


        btnEmergencyContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PoliceEmergencyContacts.class));
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignIn.class));
            }
        });
    }
}
