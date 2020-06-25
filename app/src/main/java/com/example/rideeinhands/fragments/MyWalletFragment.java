package com.example.rideeinhands.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.rideeinhands.R;
import com.example.rideeinhands.databinding.FragmentMyWalletBinding;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private AlertDialog alertDialog;

    public MyWalletFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyWalletBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        binding.addCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_credits, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(true);
                EditText editText = (EditText) dialogView.findViewById(R.id.editText);
                ImageView imageView = (ImageView) dialogView.findViewById(R.id.close);
                Button btn = (Button) dialogView.findViewById(R.id.btn);

                alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(true);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!editText.getText().toString().isEmpty()) {
                            collectionReference.document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        Long existingValue = documentSnapshot.getLong("Total");
                                        Long newTotal = Long.parseLong(editText.getText().toString()) + existingValue;
                                        collectionReference.document(FirebaseAuth.getInstance().getUid()).update("Total", newTotal).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                alertDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
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
