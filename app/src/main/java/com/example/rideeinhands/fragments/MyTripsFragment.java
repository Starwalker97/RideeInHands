package com.example.rideeinhands.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rideeinhands.R;
import com.example.rideeinhands.TripDetailActivity;
import com.example.rideeinhands.models.TripModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class MyTripsFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public static ArrayList<TripModel> activetripsList;
    FirestoreRecyclerAdapter<TripModel, TripsViewHolder> firestoreRecyclerAdapter;

    public MyTripsFragment() {
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

        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        recyclerView = view.findViewById(R.id.trip_list);
        firebaseFirestore = FirebaseFirestore.getInstance();
        activetripsList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        Query query = firebaseFirestore.collection("Trips").document(firebaseAuth.getCurrentUser().getUid())
                .collection("Pending");
        FirestoreRecyclerOptions<TripModel> options = new FirestoreRecyclerOptions.Builder<TripModel>()
                .setQuery(query, TripModel.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<TripModel, TripsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TripsViewHolder tripsViewHolder, int i, @NonNull TripModel tripModel) {
                tripsViewHolder.setName(tripModel.getName());
                tripsViewHolder.setDate(tripModel.getDate());
                tripsViewHolder.setDestination(tripModel.getDestination());

                tripModel.setTripId(getSnapshots().getSnapshot(i).getId());
                activetripsList.add(tripModel);

            }

            @NonNull
            @Override
            public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_trip_layout, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), TripDetailActivity.class);
                        int itemPosition = recyclerView.getChildLayoutPosition(view);
                        intent.putExtra("position", itemPosition);
                        intent.putExtra("whichActivity", "MyTripsFragment");
                        startActivity(intent);
                    }
                });
                return new TripsViewHolder(view);
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(firestoreRecyclerAdapter);


        return view;
    }

    private class TripsViewHolder extends RecyclerView.ViewHolder {
        private View view;


        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void setName(String name) {
            TextView textView = view.findViewById(R.id.tripName);
            textView.setText(name);
        }

        void setDestination(String destination) {
            TextView textView = view.findViewById(R.id.destination);
            textView.setText(destination);
        }

        void setDate(String date) {
            TextView textView = view.findViewById(R.id.date);
            textView.setText(date);

        }
    }

}
