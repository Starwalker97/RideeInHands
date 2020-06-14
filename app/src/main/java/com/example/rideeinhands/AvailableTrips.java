package com.example.rideeinhands;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rideeinhands.adminactivities.UserDetailsActivity;
import com.example.rideeinhands.adminfragments.UserManagement;
import com.example.rideeinhands.adminmodels.User;
import com.example.rideeinhands.databinding.ActivityAvailableTripsBinding;
import com.example.rideeinhands.models.TripModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AvailableTrips extends AppCompatActivity {
    public static ArrayList<TripModel> tripsList;
    ActivityAvailableTripsBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    LatLng originLatLng, destinationLatLng;
    String no_of_passengers;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter<TripModel, AvailableTrips.TripsViewHolder> firestoreRecyclerAdapter;
    private ArrayList<TripModel> Trips;

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
        binding = ActivityAvailableTripsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        tripsList = new ArrayList<>();
        originLatLng = new LatLng(getIntent().getDoubleExtra("startLocationLat",0.0),
                getIntent().getDoubleExtra("startLocationLong",0.0));
        destinationLatLng = new LatLng(getIntent().getDoubleExtra("destinationLocationLat",0.0),
                getIntent().getDoubleExtra("destinationLocationLong",0.0));
        no_of_passengers = getIntent().getStringExtra("no_of_passengers");

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Available Trips");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = binding.recyclerView;
        firebaseFirestore = FirebaseFirestore.getInstance();
        Trips = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(AvailableTrips.this);
        recyclerView.setLayoutManager(layoutManager);
        Query query = firebaseFirestore.collection("Trips")
                .whereEqualTo("DestinationLocation",destinationLatLng.latitude+","+destinationLatLng.longitude)
                .whereGreaterThanOrEqualTo("NumberOfPassengers", Integer.parseInt(no_of_passengers))
                .whereEqualTo("Status","pending");
        FirestoreRecyclerOptions<TripModel> options = new FirestoreRecyclerOptions.Builder<TripModel>()
                .setQuery(query, TripModel.class)
                .build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<TripModel, AvailableTrips.TripsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AvailableTrips.TripsViewHolder TripsViewHolder, int i, @NonNull TripModel Trip) {
                String[] splitone = Trip.getStartLocation().split(",");
                if (checkForArea(1,originLatLng,new LatLng(Double.parseDouble(splitone[0]),Double.parseDouble(splitone[1])))) {
                    TripsViewHolder.setDate(Trip.getDate());
                    TripsViewHolder.setName(Trip.getName());
                    TripsViewHolder.setDestination(Trip.getDestination());
                    Trip.setTripId(getSnapshots().getSnapshot(i).getId());
                    tripsList.add(Trip);
                } else {
                    TripsViewHolder.itemView.setVisibility(View.GONE);
                    TripsViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }
            @NonNull
            @Override
            public AvailableTrips.TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_trip_layout, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AvailableTrips.this, TripDetailActivity.class);
                        intent.putExtra("whichActivity","AvailableTrips");
                        intent.putExtra("passengers",no_of_passengers);
                        intent.putExtra("position",recyclerView.getChildLayoutPosition(v));
                        startActivity(intent);
                    }
                });
                return new AvailableTrips.TripsViewHolder(view);
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(firestoreRecyclerAdapter);



    }
    private class TripsViewHolder extends RecyclerView.ViewHolder {
        private View view;
        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
        void setDate(String date){
            TextView textView = view.findViewById(R.id.date);
            textView.setText(date);
        }
        void setDestination(String destination) {
            TextView textView = view.findViewById(R.id.destination);
            textView.setText(destination);
        }

        void setName(String name) {
            TextView textView = view.findViewById(R.id.tripName);
            textView.setText(name);
        }


    }
    private boolean checkForArea(int rad, LatLng fromPosition, LatLng toPosition) {
        Location locationA = new Location("point A");
        locationA.setLatitude(fromPosition.latitude);
        locationA.setLongitude(fromPosition.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(toPosition.latitude);
        locationB.setLongitude(toPosition.longitude);
        int distance = (int) locationA.distanceTo(locationB);
        if (distance / 1000 <= rad)
            return true;
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}
