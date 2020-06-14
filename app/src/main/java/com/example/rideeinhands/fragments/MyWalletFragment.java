package com.example.rideeinhands.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rideeinhands.R;
import com.example.rideeinhands.databinding.FragmentMyWalletBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;


public class MyWalletFragment extends Fragment {

    FragmentMyWalletBinding binding;
    CollectionReference collectionReference;

    public MyWalletFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyWalletBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        collectionReference = FirebaseFirestore.getInstance().collection("Wallets");

        collectionReference.document(FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {


                if (documentSnapshot.exists()){
                    binding.totalAmount.setText(documentSnapshot.getLong("Total")+"");
                }
            }
        });
    }
}
