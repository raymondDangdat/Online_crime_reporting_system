package comq.example.raymond.crimereport2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CrimeReportedDetail extends AppCompatActivity {


    //toolbar
    private android.support.v7.widget.Toolbar crime_reported_detail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_reported_detail);


        //initialize toolBar
        crime_reported_detail = findViewById(R.id.crime_reported_detail_toolbar);
        setSupportActionBar(crime_reported_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Details of crime you reported");

    }
}
