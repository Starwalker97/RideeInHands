package com.example.rideeinhands;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rideeinhands.databinding.ActivityRequestsBinding;
import com.example.rideeinhands.models.Request;
import com.example.rideeinhands.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsActivity extends AppCompatActivity {

    ActivityRequestsBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    RequestsAdapter requestsAdapter;
    ArrayList<Request> requests;
    ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        requests = new ArrayList<>();
        users = new ArrayList<>();

        firebaseFirestore.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                users.add(
                                        new User(
                                                documentSnapshot.getId(),
                                                documentSnapshot.getString("Address"),
                                                documentSnapshot.getString("DateOfBirth"),
                                                documentSnapshot.getString("EmailAddress"),
                                                documentSnapshot.getString("MobileNumber"),
                                                documentSnapshot.getString("Name"),
                                                documentSnapshot.getString("ProfilePicture")
                                        )
                                );
                            }
                            firebaseFirestore.collection("Trips")
                                    .document(firebaseUser.getUid())
                                    .collection("Pending")
                                    .document(TripDetailActivity.activeTrip.getTripId())
                                    .collection("Requests")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                                    if (documentSnapshot.getString("Status").equals("Requested")) {
                                                        requests.add(new Request(
                                                                documentSnapshot.getId()
                                                                , documentSnapshot.getString("Status")
                                                                , documentSnapshot.getString("DateTime")
                                                                , documentSnapshot.getString("PickUpPoint")
                                                        ));
                                                    }
                                                    requestsAdapter.notifyDataSetChanged();
                                                }

                                            }
                                        }
                                    });
                        }
                    }
                });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        requestsAdapter = new RequestsAdapter(requests, users);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(),
                layoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
        binding.recyclerView.setAdapter(requestsAdapter);
        binding.recyclerView.setHasFixedSize(true);

    }

    public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {

        ArrayList<Request> requests;
        ArrayList<User> users;

        public RequestsAdapter(ArrayList<Request> dataset, ArrayList<User> userSet) {
            requests = dataset;
            users = userSet;
        }

        @NonNull
        @Override
        public RequestsAdapter.RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_request_view, parent, false);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }


            });

            RequestsViewHolder vh = new RequestsViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull RequestsViewHolder holder, int position) {
            for (User user : users) {
                if (user.getUserId().equals(requests.get(position).getUserId())) {
                    holder.userName.setText(user.getName());
                    Picasso.get().load(Uri.parse(user.getProfilePicture()))
                            .placeholder(R.drawable.ic_account_circle_gray_24dp)
                            .into(holder.userImage);
                    holder.pickupPoint.setText(requests.get(position).getPickupPoint());
                }
            }

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseFirestore.collection("Trips")
                            .document(firebaseUser.getUid())
                            .collection("Pending")
                            .document(TripDetailActivity.activeTrip.getTripId())
                            .collection("Requests")
                            .document(requests.get(position).getUserId())
                            .update("Status", "Accepted")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.accept.setVisibility(View.GONE);
                                    holder.reject.setVisibility(View.GONE);
                                    holder.acceptText.setVisibility(View.VISIBLE);

                                }
                            });
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseFirestore.collection("Trips")
                            .document(firebaseUser.getUid())
                            .collection("Pending")
                            .document(TripDetailActivity.activeTrip.getTripId())
                            .collection("Requests")
                            .document(requests.get(position).getUserId())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.accept.setVisibility(View.GONE);
                                    holder.reject.setVisibility(View.GONE);
                                    holder.acceptText.setVisibility(View.VISIBLE);
                                    holder.acceptText.setText("Trip Request Deleted");
                                }
                            });
                }

            });

        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        public class RequestsViewHolder extends RecyclerView.ViewHolder {
            CircleImageView userImage;
            TextView userName;
            TextView pickupPoint;
            TextView acceptText;
            Button accept, reject;

            public RequestsViewHolder(@NonNull View itemView) {
                super(itemView);
                userImage = itemView.findViewById(R.id.userImage);
                userName = itemView.findViewById(R.id.userName);
                pickupPoint = itemView.findViewById(R.id.pickupPoint);
                accept = itemView.findViewById(R.id.accept);
                reject = itemView.findViewById(R.id.reject);
                acceptText = itemView.findViewById(R.id.acceptText);
            }
        }
    }


}
