package com.example.rideeinhands.adminactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.rideeinhands.R;
import com.example.rideeinhands.databinding.ActivityPhotoBinding;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    ActivityPhotoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        String photolink = getIntent().getStringExtra("photolink");
        Picasso.get().load(photolink).into(binding.image);

    }
}