package com.example.rideeinhands;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.TimeZone;

public class GetARideActivity extends AppCompatActivity {
    MaterialEditText no_of_passengers;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_aride);

        no_of_passengers = findViewById(R.id.no_of_passengers);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Number Of Passengers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(GetARideActivity.this, SelectLocation.class);
                intent.putExtra("whichActivity", "GetARide");
                intent.putExtra("no_of_passengers", no_of_passengers.getText().toString());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


}
