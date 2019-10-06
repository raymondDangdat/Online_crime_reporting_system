package comq.example.raymond.crimereport2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import comq.example.raymond.crimereport2.Interface.ItemClickListener;
import comq.example.raymond.crimereport2.Model.EmergencyContactModel;

public class ManageEmergencyContacts extends AppCompatActivity {
    private android.support.v7.widget.Toolbar manage_emergency_contacts_toolbar;

    private DatabaseReference database;

    private FirebaseRecyclerAdapter<EmergencyContactModel, EmergencyContactsViewHolder>adapter;
    private RecyclerView recycler_emergency_contacts;
    RecyclerView.LayoutManager layoutManager;

    private MaterialEditText editTextDistrict, editTextPhone;
    private Spinner spinnerLg;

    private EmergencyContactModel emergencyContactModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_emergency_contacts);


        database = FirebaseDatabase.getInstance().getReference().child("crimeReporting2019").child("emergencyContacts");

        //initialize toolBar
        manage_emergency_contacts_toolbar = findViewById(R.id.manage_emergency_contacts_toolbar);
        setSupportActionBar(manage_emergency_contacts_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Manage Emergency Contacts");


        recycler_emergency_contacts = findViewById(R.id.recycler_emergency_contacts);
        recycler_emergency_contacts.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_emergency_contacts.setLayoutManager(layoutManager);


        loadContacts();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

    }

    private void loadContacts() {
        FirebaseRecyclerOptions<EmergencyContactModel>options = new FirebaseRecyclerOptions.Builder<EmergencyContactModel>()
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

    private void showDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManageEmergencyContacts.this);
        alertDialog.setTitle("Add new emergency");
        alertDialog.setMessage("Please fill the information correctly");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_emergency_contact_layout = inflater.inflate(R.layout.add_emergency_contact_layout, null);

        editTextPhone = add_emergency_contact_layout.findViewById(R.id.edt_phone);
        editTextDistrict = add_emergency_contact_layout.findViewById(R.id.edtDsitrict);
        spinnerLg = add_emergency_contact_layout.findViewById(R.id.spinner_lg);

        // btnSelect = add_chalet_layout.findViewById(R.id.btnSelect);
        //btnUpload = add_chalet_layout.findViewById(R.id.btnUpload);

        alertDialog.setView(add_emergency_contact_layout);
        alertDialog.setIcon(R.drawable.ic_add_circle_black_24dp);

//        btnSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });


//        btnUpload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadImage();
//            }
//        });

        //set button
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String lg = spinnerLg.getSelectedItem().toString();
                String district =  editTextDistrict.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone) || phone.length() > 11){
                    Toast.makeText(ManageEmergencyContacts.this, "Make sure you enter a valid phone number", Toast.LENGTH_SHORT).show();
                }else if (lg.equals("Select L.G.A")){
                    Toast.makeText(ManageEmergencyContacts.this, "Please select valid LGA", Toast.LENGTH_SHORT).show();
                }else {
                    emergencyContactModel = new EmergencyContactModel(lg, district, phone);
                    database.push().setValue(emergencyContactModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ManageEmergencyContacts.this, "Contact Added successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ManageEmergencyContacts.this, ManageEmergencyContacts.class));

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManageEmergencyContacts.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
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
