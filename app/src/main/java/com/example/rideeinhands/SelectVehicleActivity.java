package com.example.rideeinhands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rideeinhands.adminfragments.UserManagement;
import com.example.rideeinhands.adminmodels.User;
import com.example.rideeinhands.databinding.ActivitySelectVehicleBinding;
import com.example.rideeinhands.fragments.MyVehiclesFragment;
import com.example.rideeinhands.models.Vehicle;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class SelectVehicleActivity extends AppCompatActivity {

    ActivitySelectVehicleBinding binding;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public static ArrayList<Vehicle> vehiclesList;
    FirestoreRecyclerAdapter<Vehicle, SelectVehicleActivity.VehicleViewHolder> firestoreRecyclerAdapter;

    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firestoreRecyclerAdapter != null) {
            firestoreRecyclerAdapter.stopListening();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectVehicleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        recyclerView = view.findViewById(R.id.recycler_view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        vehiclesList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(SelectVehicleActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        Query query = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("Vehicle");
        FirestoreRecyclerOptions<Vehicle> options = new FirestoreRecyclerOptions.Builder<Vehicle>()
                .setQuery(query, Vehicle.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Vehicle, SelectVehicleActivity.VehicleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SelectVehicleActivity.VehicleViewHolder vehicleViewHolder, int i, @NonNull Vehicle vehicle) {
                vehicleViewHolder.setVehicleName(vehicle.getCompany() +" "+ vehicle.getModel());
                vehicleViewHolder.setRegNumber(vehicle.getRegisterationNumber());
                vehicleViewHolder.setImage(vehicle.getType());
                vehicle.setId(getSnapshots().getSnapshot(i).getId());
                vehiclesList.add(vehicle);

            }
            public void deleteItem(int position) {
                getSnapshots().getSnapshot(position).getReference().delete();
            }


            @NonNull
            @Override
            public SelectVehicleActivity.VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_single_layout, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SelectVehicleActivity.this,SelectLocation.class);
                        intent.putExtra("whichActivity",getIntent().getStringExtra("whichActivity"));
                        intent.putExtra("tripName",getIntent().getStringExtra("tripName"));
                        intent.putExtra("tripDetail",getIntent().getStringExtra("tripDetail"));
                        intent.putExtra("tripDate",getIntent().getStringExtra("tripDate"));
                        intent.putExtra("tripTime",getIntent().getStringExtra("tripTime"));
                        intent.putExtra("no_of_passengers",getIntent().getStringExtra("no_of_passengers"));
                        intent.putExtra("vehicle",firestoreRecyclerAdapter.getSnapshots().getSnapshot(recyclerView.getChildLayoutPosition(v))
                                .getId());
                        startActivity(intent);

                    }
                });

                return new SelectVehicleActivity.VehicleViewHolder(view);
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(firestoreRecyclerAdapter);



    }
    private class VehicleViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

        }

        void setVehicleName(String name) {
            TextView textView = view.findViewById(R.id.name);
            textView.setText(name);
        }

        void setRegNumber(String destination) {
            TextView textView = view.findViewById(R.id.reg_number);
            textView.setText(destination);
        }

        void setImage(String type){
            ImageView imageView = view.findViewById(R.id.icon);
            if (type.equals("Car")){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.car_icon));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.motorcycle_icon));

            }
        }


    }
}