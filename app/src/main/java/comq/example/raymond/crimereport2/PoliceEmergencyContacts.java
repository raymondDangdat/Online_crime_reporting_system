package comq.example.raymond.crimereport2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import comq.example.raymond.crimereport2.Interface.ItemClickListener;
import comq.example.raymond.crimereport2.Model.EmergencyContactModel;

public class PoliceEmergencyContacts extends AppCompatActivity {


    //toolbar
    private android.support.v7.widget.Toolbar police_emergency_contacts_toolbar;

    private DatabaseReference  database;


    private FirebaseRecyclerAdapter<EmergencyContactModel, EmergencyContactsViewHolder> adapter;
    private RecyclerView recycler_emergency_contacts;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_emergency_contacts);


        database = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("emergencyContacts");
        database.keepSynced(true);

        //database = FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //initialize toolBar
        police_emergency_contacts_toolbar = findViewById(R.id.police_emergenct_contact_toolbar);
        setSupportActionBar(police_emergency_contacts_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Police Emergency Contacts");

        recycler_emergency_contacts = findViewById(R.id.recycler_emergency_contacts);
        recycler_emergency_contacts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_emergency_contacts.setLayoutManager(layoutManager);


        loadContacts();



    }

    private void loadContacts() {

        FirebaseRecyclerOptions<EmergencyContactModel> options = new FirebaseRecyclerOptions.Builder<EmergencyContactModel>()
                .setQuery(database, EmergencyContactModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<EmergencyContactModel, EmergencyContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EmergencyContactsViewHolder holder, int position, @NonNull EmergencyContactModel model) {
                holder.txtDistrict.setText("District: "+model.getDistrict());
                holder.txtLg.setText("L.G.A: " + model.getLg());
                holder.txtPhone.setText("Phone: "+model.getPhone());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        String contactKey = adapter.getRef(position).getKey();
                        callEmergencyContact(contactKey);

                    }
                });
            }

            @NonNull
            @Override
            public EmergencyContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emergency_contact_layout_display, viewGroup,false);
                EmergencyContactsViewHolder viewHolder = new EmergencyContactsViewHolder(view);
                return viewHolder;
            }
        };
        recycler_emergency_contacts.setAdapter(adapter);
        adapter.startListening();
    }

    private void callEmergencyContact(String contactKey) {
        database.child(contactKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EmergencyContactModel emergencyContactModel = dataSnapshot.getValue(EmergencyContactModel.class);

                final String phone = emergencyContactModel.getPhone().toString();
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:"+phone));
                if (ActivityCompat.checkSelfPermission(PoliceEmergencyContacts.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(phoneIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class EmergencyContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtDistrict, txtLg, txtPhone;
        private ItemClickListener itemClickListener;
        public EmergencyContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDistrict = itemView.findViewById(R.id.txt_district);
            txtLg = itemView.findViewById(R.id.txt_lg);
            txtPhone = itemView.findViewById(R.id.txt_phone);

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
