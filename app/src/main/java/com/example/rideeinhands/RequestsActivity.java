package com.example.rideeinhands;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rideeinhands.adminactivities.UserDetailsActivity;
import com.example.rideeinhands.databinding.ActivityRequestsBinding;
import com.example.rideeinhands.models.Request;
import com.example.rideeinhands.models.Request;
import com.example.rideeinhands.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsActivity extends AppCompatActivity {

    ActivityRequestsBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter<Request, RequestsActivity.RequestsViewHolder> firestoreRecyclerAdapter;

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
        binding = ActivityRequestsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        String docID = getIntent().getStringExtra("docID");
        recyclerView = binding.recyclerView;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(RequestsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsActivity.this, TripActivity.class);
                intent.putExtra("tripID", docID);
                startActivity(intent);
            }
        });
        Query query = firebaseFirestore.collection("Requests")
                .whereEqualTo("To",FirebaseAuth.getInstance().getUid())
                .whereEqualTo("TripID", docID);
        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Request, RequestsActivity.RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsActivity.RequestsViewHolder RequestsViewHolder, int i, @NonNull Request request) {
                firebaseFirestore.collection("Users")
                        .document(request.getFrom())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                RequestsViewHolder.setImage(documentSnapshot.getString("ProfilePicture"));
                                RequestsViewHolder.setName(documentSnapshot.getString("Name"));
                            }
                        });
                RequestsViewHolder.setStatus(request.getStatus());


//                    RequestsViewHolder.itemView.setVisibility(View.GONE);
//                    RequestsViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

            }
            @NonNull
            @Override
            public RequestsActivity.RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_request_view, parent, false);
                view.findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("Requests").document(getSnapshots().getSnapshot(recyclerView.getChildLayoutPosition(view)).getId())
                                .update("Status","Accepted");
                        firebaseFirestore.collection("Trips").document(docID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Integer passengers = documentSnapshot.getLong("Passengers").intValue();
                                firebaseFirestore.collection("Requests").document(getSnapshots().getSnapshot(recyclerView.getChildLayoutPosition(view)).getId())
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Integer passenger2 = passengers - documentSnapshot.getLong("NoOfPassengers").intValue();
                                        firebaseFirestore.collection("Trips").document(getSnapshots().getSnapshot(recyclerView.getChildLayoutPosition(view)).getId())
                                                .update("NoOfPassengers", passenger2);
                                    }
                                });

                            }
                        });

                    }
                });
                view.findViewById(R.id.reject).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("Requests").document(getSnapshots().getSnapshot(recyclerView.getChildLayoutPosition(view)).getId())
                                .delete();
                    }
                });
                return new RequestsActivity.RequestsViewHolder(view);
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(firestoreRecyclerAdapter);
        firebaseFirestore.collection("Requests")
                .whereEqualTo("To",FirebaseAuth.getInstance().getUid())
                .whereEqualTo("TripID", docID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                int i = 1;
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    if (documentSnapshot.getString("Status").equals("Pending")){
                        i = 0;
                    }
                }
                if (i==1){
                    findViewById(R.id.btn).setEnabled(true);
                }
            }
        });
    }
    private class RequestsViewHolder extends RecyclerView.ViewHolder {
        private View view;
        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
        void setName(String name){
            TextView textView = view.findViewById(R.id.userName);
            textView.setText(name);
        }
        void setImage(String image) {
           CircleImageView circleImageView = view.findViewById(R.id.userImage);
            ProgressBar progressBar = view.findViewById(R.id.progress_circular2);
            progressBar.setVisibility(View.VISIBLE);
            Picasso.get().load(Uri.parse(image)).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(Uri.parse(image)).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RequestsActivity.this, "Failed to load some images", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        void setStatus(String status) {
            TextView textView = view.findViewById(R.id.acceptText);
            if (status.equals("Accepted")){
                textView.setVisibility(View.VISIBLE);
                view.findViewById(R.id.accept).setVisibility(View.GONE);
                view.findViewById(R.id.reject).setVisibility(View.GONE);
            } else if (status.equals("Pending")) {
                textView.setVisibility(View.GONE);
                view.findViewById(R.id.accept).setVisibility(View.VISIBLE);
                view.findViewById(R.id.reject).setVisibility(View.VISIBLE);
            } else if (status.equals("Arrived")){
                textView.setText("This passenger has arrived to his destination");
            }
        }


    }


}
