package com.example.rideeinhands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rideeinhands.databinding.ActivityAddVehicleBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddVehicleActivity extends AppCompatActivity {

    ActivityAddVehicleBinding binding;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVehicleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Add New Vehicle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = FirebaseFirestore.getInstance().collection("License")
                .document(FirebaseAuth.getInstance().getUid());

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("Company", binding.Company.getText().toString());
                map.put("Model", binding.Model.getText().toString());
                map.put("RegisterationNumber", binding.registrationNumber.getText().toString());
                map.put("Type", binding.type.getSelectedItem().toString());
                binding.submit.setBackgroundTintList(getResources().getColorStateList(R.color.solidCircle));
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.submit.setClickable(false);
                if (map.get("Type").equals("Car")){
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult()!=null && task.getResult().exists()){
                                binding.submit.setClickable(true);
                                binding.submit.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                                binding.progressCircular.setVisibility(View.GONE);
                                String status = task.getResult().get("Status").toString();
                                if (status.equals("Not Accepted Yet")){
                                    Toast.makeText(AddVehicleActivity.this, "Your License is not approved by administrator yet",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else if (status.equals("Denied")){
                                    Toast.makeText(AddVehicleActivity.this, "Your license is denied by administrator." +
                                            " Delete it and then upload a valid license or contact support team for more information.", Toast.LENGTH_LONG).show();
                                } else {
                                    addVehicle(map);
                                }
                            }
                            else {
                                Toast.makeText(AddVehicleActivity.this,
                                        "You must upload a valid driving license for adding a car",
                                        Toast.LENGTH_SHORT).show();
                                binding.submit.setClickable(true);
                                binding.submit.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                                binding.progressCircular.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.submit.setClickable(true);
                            binding.submit.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                            binding.progressCircular.setVisibility(View.GONE);
                            Toast.makeText(AddVehicleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    addVehicle(map);
                }

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addVehicle(HashMap<String,String> map){


        CollectionReference collectionReference = firebaseFirestore.collection("Users")
                .document(FirebaseAuth.getInstance().getUid()).collection("Vehicle");
        collectionReference.document(collectionReference.document().getId()).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.submit.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                        binding.progressCircular.setVisibility(View.GONE);
                        binding.submit.setClickable(true);
                        if (task.isSuccessful()){
                            Toast.makeText(AddVehicleActivity.this, "Vehicle Successfully Added", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(AddVehicleActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.submit.setClickable(true);
                binding.submit.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                binding.progressCircular.setVisibility(View.GONE);
                Toast.makeText(AddVehicleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
