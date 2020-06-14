package com.example.rideeinhands.adminactivities;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rideeinhands.databinding.ActivityUserDetailsBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {

    ActivityUserDetailsBinding binding;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mFunctions = FirebaseFunctions.getInstance();
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.progressCircular2.setVisibility(View.VISIBLE);
        binding.address.setText(getIntent().getStringExtra("address"));
        binding.userName.setText(getIntent().getStringExtra("userName"));
        binding.email.setText(getIntent().getStringExtra("email"));
        binding.dob.setText(getIntent().getStringExtra("dob"));
        binding.role.setText(getIntent().getStringExtra("role"));
        if (getIntent().getStringExtra("status").equals("true")){
            binding.status.setText("Disabled");
        }
        else {
            binding.status.setText("Enabled");

        }


        Picasso.get().load(Uri.parse(getIntent().getStringExtra("profilePicture"))).networkPolicy(NetworkPolicy.OFFLINE).into(binding.circleImageView2, new Callback() {
            @Override
            public void onSuccess() {
                binding.progressCircular2.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(Uri.parse(getIntent().getStringExtra("profilePicture"))).into(binding.circleImageView2, new Callback() {
                    @Override
                    public void onSuccess() {
                        binding.progressCircular2.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        binding.progressCircular2.setVisibility(View.GONE);
                        Toast.makeText(UserDetailsActivity.this, "Failed to load some images", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        binding.mobile.setText(getIntent().getStringExtra("mobile"));
        registerForContextMenu(binding.options);
        binding.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.options.showContextMenu();
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Option");
        menu.add(0, 1, 0, "Enable/Disable this user");
        menu.add(0, 2, 0, "Make this user an admin");
        menu.add(0, 3, 0, "Delete this user");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 1:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to disable this user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                disableUser().addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<HashMap<String, String>> task) {
                                        if (task.isSuccessful()) {
                                            if (binding.status.getText().equals("Enabled")){
                                                binding.status.getText().equals("Disabled");
                                            }
                                            else {
                                                binding.status.getText().equals("Enabled");
                                            }
                                            Toast.makeText(UserDetailsActivity.this, "Successfully disabled user", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(UserDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(UserDetailsActivity.this, "Operation Cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
            break;
            case 2:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to make this user an admin?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users")
                                        .document(getIntent().getStringExtra("uid"));
                                documentReference.update("Role","admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(UserDetailsActivity.this, "This user is now an admin", Toast.LENGTH_SHORT).show();
                                        } else{
                                            Toast.makeText(UserDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(UserDetailsActivity.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
            break;
            case 3:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUser().addOnCompleteListener(new OnCompleteListener<HashMap<String, String>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<HashMap<String, String>> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(UserDetailsActivity.this, "Successfully deleted user", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(UserDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(UserDetailsActivity.this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
            break;
        }
        return true;
    }
    private Task<HashMap<String,String>> disableUser() {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", getIntent().getStringExtra("uid"));
        return mFunctions
                .getHttpsCallable("disableUser")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String,String>>() {
                    @Override
                    public HashMap<String,String> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String,String> result = (HashMap<String,String>)task.getResult().getData();
                        return result;
                    }
                });
    }
    private Task<HashMap<String,String>> deleteUser() {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", getIntent().getStringExtra("uid"));
        return mFunctions
                .getHttpsCallable("deleteUser")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String,String>>() {
                    @Override
                    public HashMap<String,String> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String,String> result = (HashMap<String,String>)task.getResult().getData();
                        return result;
                    }
                });
    }
}
