package com.example.rideeinhands.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rideeinhands.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.libizo.CustomEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class MyAccountFragment extends Fragment {
    CustomEditText username, emailId, phoneNumber, address, dateOfBirth, password;
    CircleImageView userImg;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    private Uri mCropImageUri;
    private Uri resultUri;
    private String downloadUri;

    public MyAccountFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        username = view.findViewById(R.id.name);
        emailId = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phone_number);
        address = view.findViewById(R.id.address);
        dateOfBirth = view.findViewById(R.id.dateOfBirth);
        password = view.findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        userImg = view.findViewById(R.id.userImage);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Password Reset Email sent to your email account", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        DocumentReference documentReference = db.collection("Users")
                .document(firebaseAuth.getCurrentUser().getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                Picasso.get().load(Uri.parse(snapshot.getString("ProfilePicture")))
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(userImg);

                username.setText(snapshot.getString("Name"));
                emailId.setText(snapshot.getString("EmailAddress"));
                phoneNumber.setText(snapshot.getString("MobileNumber"));
                address.setText(snapshot.getString("Address"));
                dateOfBirth.setText(snapshot.getString("DateOfBirth"));
                downloadUri = snapshot.getString("ProfilePicture");
            }
        });



        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(userImg.getRootView());

            }
        });

        submit(view);

        return view;

    }
    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(getContext(),this);

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
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
                startCropImageActivity(imageUri);

            }

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                userImg.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .start(getContext(),this);
    }

    private void applyArialFont(TextView textView){
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/arial.ttf");
        textView.setTypeface(custom_font);
    }

    private void submit(View view){
        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, String> map = new HashMap<>();
                map.put("Name", username.getText().toString());
                map.put("EmailAddress", emailId.getText().toString());
                map.put("MobileNumber", phoneNumber.getText().toString());
                map.put("Address", address.getText().toString());
                map.put("DateOfBirth", dateOfBirth.getText().toString());

                if (resultUri!=null){
                    setPictureAndData(map);
                }
                else {
                    setData(map, downloadUri);
                }




            }
        });
    }

    private void setPictureAndData(final Map<String, String> map){
        StorageReference Sref = storageReference.child("images/"+firebaseAuth.getCurrentUser().getUid()+".jpg");

        Sref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUri = uri.toString();
                        setData(map, downloadUri);

                    }
                });
            }
        });
    }

    private void setData(Map<String, String> map, String downloadUri){
        map.put("ProfilePicture", downloadUri);
        firebaseAuth.getCurrentUser().updateEmail(emailId.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        }
                        else {

                            Toast.makeText(getContext(),"Email Address cannot be changed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection("Users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
