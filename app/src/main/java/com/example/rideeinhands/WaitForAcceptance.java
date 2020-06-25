package com.example.rideeinhands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import com.example.rideeinhands.databinding.ActivityWaitForAcceptanceBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class WaitForAcceptance extends AppCompatActivity {
    ActivityWaitForAcceptanceBinding binding;
    CollectionReference collectionReference;
    boolean cancelRequest = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaitForAcceptanceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        collectionReference = FirebaseFirestore.getInstance().collection("Requests");
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest = true;
                collectionReference.document(getIntent().getStringExtra("requestID")).delete();
            }
        });
        collectionReference.document(getIntent().getStringExtra("requestID")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()&&documentSnapshot.getString("Status").equals("Accepted")){
                    Intent intent = new Intent(WaitForAcceptance.this, HeadTowardsActivity.class);
                    intent.putExtra("tripID", documentSnapshot.getString("TripID"));
                    intent.putExtra("requestID", documentSnapshot.getId());
                    startActivity(intent);
                } else if (!documentSnapshot.exists()){
                    Toast.makeText(WaitForAcceptance.this, "Your trip request has been rejected", Toast.LENGTH_SHORT).show(); }
                    //finish();
            }
        });

    }
}