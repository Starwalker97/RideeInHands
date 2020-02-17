package com.example.rideeinhands;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    MaterialEditText username, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username = findViewById(R.id.username);
        number = findViewById(R.id.number);

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!username.getText().toString().isEmpty()&&!number.getText().toString().isEmpty()) {

                    if (number.getText().toString().matches("^((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$")){
                        Intent intent = new Intent(SignUpActivity.this, RegisterActivity.class);
                        intent.putExtra("email", username.getText().toString());
                        intent.putExtra("number", number.getText().toString());
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Enter a valid phone number from Pakistan (+92xxxxxxxxxx)", Toast.LENGTH_SHORT).show();
                    }


                }
                else {
                    Toast.makeText(SignUpActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
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
}
