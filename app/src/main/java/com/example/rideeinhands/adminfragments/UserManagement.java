package com.example.rideeinhands.adminfragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rideeinhands.R;
import com.example.rideeinhands.adminactivities.UserDetailsActivity;
import com.example.rideeinhands.adminmodels.User;
import com.example.rideeinhands.adminmodels.User;
import com.example.rideeinhands.databinding.FragmentUserManagementBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserManagement extends Fragment {
    FragmentUserManagementBinding binding;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public static ArrayList<User> Users;
    FirestoreRecyclerAdapter<User, UserManagement.UsersViewHolder> firestoreRecyclerAdapter;

    public UserManagement() {
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
        binding = FragmentUserManagementBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        recyclerView = binding.recyclerView;
        firebaseFirestore = FirebaseFirestore.getInstance();
        Users = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        Query query = firebaseFirestore.collection("Users").whereEqualTo("Role","user");
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<User, UserManagement.UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserManagement.UsersViewHolder UsersViewHolder, int i, @NonNull User User) {
                    UsersViewHolder.setImage(User.getProfilePicture());
                    UsersViewHolder.setName(User.getName());
                    Users.add(User);
            }
            @NonNull
            @Override
            public UserManagement.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(getContext(), UserDetailsActivity.class);
                        int position = recyclerView.getChildLayoutPosition(view);
                        intent.putExtra("profilePicture",firestoreRecyclerAdapter.getItem(position).getProfilePicture());
                        intent.putExtra("userName",firestoreRecyclerAdapter.getItem(position).getName());
                        intent.putExtra("email",firestoreRecyclerAdapter.getItem(position).getEmailAddress());
                        intent.putExtra("address",firestoreRecyclerAdapter.getItem(position).getAddress());
                        intent.putExtra("mobile",firestoreRecyclerAdapter.getItem(position).getMobileNumber());
                        intent.putExtra("dob",firestoreRecyclerAdapter.getItem(position).getDateOfBirth());
                        intent.putExtra("role",firestoreRecyclerAdapter.getItem(position).getRole());
                        intent.putExtra("uid",firestoreRecyclerAdapter.getSnapshots().getSnapshot(position).getId());
                        intent.putExtra("status",firestoreRecyclerAdapter.getItem(position).getDisabled());
                        startActivity(intent);
                    }
                });
                return new UserManagement.UsersViewHolder(view);
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(firestoreRecyclerAdapter);
        return view;
    }
    private class UsersViewHolder extends RecyclerView.ViewHolder {
        private View view;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
        void setImage(String image) {
            CircleImageView circleImageView = view.findViewById(R.id.circleImageView);
            ProgressBar progressBar = view.findViewById(R.id.progress_circular);
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
                            Toast.makeText(getContext(), "Failed to load some images", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }

        void setName(String name) {
           TextView textView = view.findViewById(R.id.textView4);
           textView.setText(name);
        }


    }

}
