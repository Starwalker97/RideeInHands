package com.example.rideeinhands;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText name, address, dateOfBirth, weight, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        dateOfBirth = findViewById(R.id.dateOfBirth);


        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!address.getText().toString().isEmpty()&&!dateOfBirth.getText().toString().isEmpty()) {
                    if (dateOfBirth.getText().toString().matches("^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$")){
                    Intent intent = new Intent(RegisterActivity.this, PictureActivity.class);
                    intent.putExtra("email", getIntent().getStringExtra("email"));
                    intent.putExtra("number", getIntent().getStringExtra("number"));
                    intent.putExtra("name",name.getText().toString());
                    intent.putExtra("address", address.getText().toString());
                    intent.putExtra("dateOfBirth", dateOfBirth.getText().toString());

                    startActivity(intent);
                    }else {
                        Toast.makeText(RegisterActivity.this, "Enter valid date of birth (DD/MM/YYYY)", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
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
