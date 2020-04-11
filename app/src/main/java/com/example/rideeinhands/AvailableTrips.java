package com.example.rideeinhands;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rideeinhands.databinding.ActivityAvailableTripsBinding;
import com.example.rideeinhands.models.TripModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AvailableTrips extends AppCompatActivity {
    public static ArrayList<TripModel> tripsList;
    ActivityAvailableTripsBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvailableTripsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Available Trips");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        tripsList = SelectLocation.tripsList;
        binding.recyclerView.setHasFixedSize(true);
        AvailableTripsAdapter availableTripsAdapter = new AvailableTripsAdapter(tripsList);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(),
                layoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
        binding.recyclerView.setAdapter(availableTripsAdapter);


    }

    public class AvailableTripsAdapter extends RecyclerView.Adapter<AvailableTripsAdapter.MyViewHolder> {
        AvailableTrips availableTrips;
        private ArrayList<TripModel> mDataset;


        // Provide a suitable constructor (depends on the kind of dataset)
        public AvailableTripsAdapter(ArrayList<TripModel> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_trip_layout, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = binding.recyclerView.getChildLayoutPosition(v);
                    Intent intent = new Intent(AvailableTrips.this, TripDetailActivity.class);
                    intent.putExtra("position", itemPosition);
                    intent.putExtra("whichActivity", "AvailableTrips");
                    intent.putExtra("PickupPoint", getIntent().getStringExtra("PickupPoint"));
                    startActivity(intent);
                }
            });
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.tripName.setText(mDataset.get(position).getName());
            holder.date.setText(mDataset.get(position).getDate());
            holder.destination.setText(mDataset.get(position).getDestination());


        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView tripName;
            public TextView destination;
            public TextView date;

            public MyViewHolder(View view) {
                super(view);
                this.tripName = view.findViewById(R.id.tripName);
                this.destination = view.findViewById(R.id.destination);
                this.date = view.findViewById(R.id.date);


            }
        }
    }

}
