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
import android.widget.TextView;
import android.widget.Toast;

import com.example.rideeinhands.R;
import com.example.rideeinhands.TripDetailActivity;
import com.example.rideeinhands.databinding.FragmentLicensesBinding;
import com.example.rideeinhands.adminmodels.License;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropOverlayView;

import java.util.ArrayList;

public class LicensesFragment extends Fragment {
    FragmentLicensesBinding binding;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    public static ArrayList<License> licenses;
    FirestoreRecyclerAdapter<License, LicensesFragment.LicensesViewHolder> firestoreRecyclerAdapter;

    public LicensesFragment() {
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
        binding = FragmentLicensesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = view.findViewById(R.id.recycler_view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        licenses = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        Query query = firebaseFirestore.collection("License");
        FirestoreRecyclerOptions<License> options = new FirestoreRecyclerOptions.Builder<License>()
                .setQuery(query, License.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<License, LicensesFragment.LicensesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LicensesFragment.LicensesViewHolder LicensesViewHolder, int i, @NonNull License License) {
                LicensesViewHolder.setImage(License.getPicture());
                LicensesViewHolder.setStatus(License.getStatus());
                licenses.add(License);

            }


            @NonNull
            @Override
            public LicensesFragment.LicensesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.license_single_layout, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                return new LicensesFragment.LicensesViewHolder(view);
            }
        };
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(firestoreRecyclerAdapter);

        return view;
    }
    private class LicensesViewHolder extends RecyclerView.ViewHolder {
        private View view;
        CardView cardView;

        public LicensesViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cardView =  itemView.findViewById(R.id.cardView);
            cardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.setHeaderTitle("Select option to proceed");
                    menu.add(getAdapterPosition(),120,0,"View Photo");
                    menu.add(getAdapterPosition(),121,1,"Approve");
                    menu.add(getAdapterPosition(),122,2,"Decline");
                }
            });
        }

        void setImage(String image) {
            ImageView imageView = view.findViewById(R.id.licenseImage);
            Picasso.get().load(Uri.parse(image)).into(imageView);
        }

        void setStatus(String status) {
            TextView textView = view.findViewById(R.id.status);
            textView.setText(status);
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Snackbar.make(view,"Long tap the item to select from options", Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 120:

                break;
            case 121:
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("License")
                        .document(firestoreRecyclerAdapter.getSnapshots().getSnapshot(item.getGroupId()).getId());
                documentReference.update("Status","Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Approved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 122:
                DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("License")
                        .document(firestoreRecyclerAdapter.getSnapshots().getSnapshot(item.getGroupId()).getId());
                documentReference1.update("Status","Denied").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Denied", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
        return super.onContextItemSelected(item);

    }
}
