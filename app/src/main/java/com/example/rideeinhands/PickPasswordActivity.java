package com.example.rideeinhands;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class PickPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private MaterialEditText password, confirmPassword;
    private String email, number, name, address, dateOfBirth;
    private String resultUri;
    private String downloadUri;
    FirebaseFirestore db;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_password);
        progressDialog = new ProgressDialog(PickPasswordActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        email = getIntent().getStringExtra("email");
        number = getIntent().getStringExtra("number");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        dateOfBirth = getIntent().getStringExtra("dateOfBirth");
        resultUri = getIntent().getStringExtra("resultUri");
        mAuth = FirebaseAuth.getInstance();
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        final Map<String,String> object = new HashMap<>();
        object.put("Name",name);
        object.put("Address",address);
        object.put("DateOfBirth",dateOfBirth);
        object.put("MobileNumber",number);
        object.put("EmailAddress",email);
        object.put("Role", "user");
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (!password.getText().toString().isEmpty()&&!confirmPassword.getText().toString().isEmpty()){
                    if (password.getText().toString().equals(confirmPassword.getText().toString())){
                        mAuth.createUserWithEmailAndPassword(email,password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            StorageReference Sref = storageReference.child("images/"+mAuth.getCurrentUser().getUid()+".jpg");
                                            Sref.putFile(Uri.parse(resultUri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            downloadUri = uri.toString();
                                                            object.put("ProfilePicture", downloadUri);
                                                            db.collection("Users")
                                                                    .document(mAuth.getCurrentUser().getUid())
                                                                    .set(object)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            sendEmailVerification();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(PickPasswordActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText(PickPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(PickPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(PickPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(PickPasswordActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }





            }
        });
        ImageView imageView = findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(PickPasswordActivity.this, "Check your mail for confirmation link", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PickPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PickPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
