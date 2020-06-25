package com.example.rideeinhands;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rideeinhands.adminactivities.MainActivityAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    TextView textView2, textView3;
    MaterialEditText email, password;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        firebaseAuth = FirebaseAuth.getInstance();


        textView2 = findViewById(R.id.textView2);
        textView3 =findViewById(R.id.textView3);


        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()){
                                                    if (firebaseAuth.getCurrentUser().isEmailVerified()){
                                                        progressDialog.dismiss();
                                                        documentReference = FirebaseFirestore.getInstance().collection("Users")
                                                                .document(firebaseAuth.getUid());
                                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid())
                                                                        .update("DeviceToken", FirebaseInstanceId.getInstance().getToken());
                                                                if (task.getResult().get("Role").equals("user")){
                                                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                                    LoginActivity.this.startActivity(mainIntent);
                                                                    LoginActivity.this.finish();
                                                                }
                                                                else if (task.getResult().get("Role").equals("admin")){
                                                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivityAdmin.class);
                                                                    LoginActivity.this.startActivity(mainIntent);
                                                                    LoginActivity.this.finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(LoginActivity.this, "This email is not verified yet.", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                                else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

    }
}
