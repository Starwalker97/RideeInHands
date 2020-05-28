package com.example.rideeinhands.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rideeinhands.AddVehicleActivity;
import com.example.rideeinhands.R;
import com.example.rideeinhands.models.Vehicle;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class MyVehiclesFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public static ArrayList<Vehicle> vehiclesList;
    FirestoreRecyclerAdapter<Vehicle, VehicleViewHolder> firestoreRecyclerAdapter;
    CollectionReference collectionReference;

    public MyVehiclesFragment() {

    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_vehicles, container, false);

        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddVehicleActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        vehiclesList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        Query query = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("Vehicle");
        FirestoreRecyclerOptions<Vehicle> options = new FirestoreRecyclerOptions.Builder<Vehicle>()
                .setQuery(query, Vehicle.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Vehicle, VehicleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VehicleViewHolder vehicleViewHolder, int i, @NonNull Vehicle vehicle) {
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
            public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_single_layout, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                                .setCancelable(false)
                                .setTitle("Confirmation")
                                .setMessage("Do you want to delete this vehicle?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteItem(recyclerView.getChildLayoutPosition(v));
                                        Toast.makeText(getContext(), "Vehicle Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(), "Operation Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        builder.show();
                    }
                });

                return new VehicleViewHolder(view);
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(firestoreRecyclerAdapter);



        return view;
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
