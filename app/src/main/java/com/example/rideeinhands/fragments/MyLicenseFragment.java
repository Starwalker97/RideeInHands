package com.example.rideeinhands.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rideeinhands.PickPasswordActivity;
import com.example.rideeinhands.R;
import com.example.rideeinhands.databinding.FragmentMyLicenseBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import javax.annotation.Nullable;

import static android.app.Activity.RESULT_OK;


public class MyLicenseFragment extends Fragment {
    FragmentMyLicenseBinding binding;
    DocumentReference documentReference;
    FirebaseAuth firebaseAuth;
    private Uri mCropImageUri;
    private Uri resultUri;


    public MyLicenseFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyLicenseBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //Initial condition of the license
        binding.noImageFound.setVisibility(View.GONE);
        binding.licenseImage.setVisibility(View.GONE);
        binding.status.setVisibility(View.GONE);
        binding.btn.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        documentReference = FirebaseFirestore.getInstance().collection("License")
                .document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.noImageFound.setVisibility(View.GONE);
                    binding.btn.setVisibility(View.GONE);
                    binding.loading.setVisibility(View.GONE);
                    binding.status.setVisibility(View.GONE);
                    binding.licenseImage.setVisibility(View.GONE);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    binding.loading.setVisibility(View.GONE);
                    binding.noImageFound.setVisibility(View.GONE);
                    binding.licenseImage.setVisibility(View.VISIBLE);
                    binding.status.setVisibility(View.VISIBLE);
                    Picasso.get().load(Uri.parse(documentSnapshot.getData().get("Picture").toString()))
                            .into(binding.licenseImage);
                    binding.status.setText(documentSnapshot.get("Status").toString());
                    binding.btn.setVisibility(View.VISIBLE);
                    binding.btn.setText("Delete License");
                } else {
                    binding.noImageFound.setVisibility(View.VISIBLE);
                    binding.btn.setVisibility(View.VISIBLE);
                    binding.loading.setVisibility(View.GONE);
                    binding.status.setVisibility(View.GONE);
                    binding.licenseImage.setVisibility(View.GONE);
                    binding.btn.setText("Add License");
                }
            }
        });

        //Button definition
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btn.getText().toString().equals("Add License")){
                    onSelectImageClick(v);
                }
                else{
                    binding.btn.setBackgroundTintList(getResources().getColorStateList(R.color.solidCircle));
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    binding.btn.setClickable(false);
                    documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                binding.btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                                binding.progressCircular.setVisibility(View.GONE);
                                binding.btn.setClickable(true);
                                binding.btn.setText("Add License");
                                Toast.makeText(getContext(), "License deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                            else {

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(getContext(),this);

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                setImage(mCropImageUri);
            } else {
                Toast.makeText(getContext(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(getContext(), imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already granted, can start crop image activity
                setImage(imageUri);
            }

        }

    }


    private void setImage(Uri imageUri){
        binding.btn.setBackgroundTintList(getResources().getColorStateList(R.color.solidCircle));
        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.btn.setClickable(false);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("license/"+firebaseAuth.getUid()+".jpg");
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String,String> map = new HashMap<>();
                        map.put("Picture",uri.toString());
                        map.put("Status","Not Accepted Yet");
                        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    binding.btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                                    binding.progressCircular.setVisibility(View.GONE);
                                    binding.btn.setClickable(true);
                                    binding.btn.setText("Delete");
                                    Toast.makeText(getContext(), "License uploaded successfully", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                                    binding.progressCircular.setVisibility(View.GONE);
                                    binding.btn.setClickable(true);
                                    binding.btn.setText("Delete");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.btn.setClickable(true);
                                binding.btn.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                                binding.progressCircular.setVisibility(View.GONE);
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
