package com.example.rideeinhands;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CreateTripActivity extends AppCompatActivity {

    MaterialEditText tripName, tripDetail, tripTime, no_of_passengers;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Trip");

        tripName = findViewById(R.id.tripName);
        tripDetail = findViewById(R.id.tripDetails);
        tripTime = findViewById(R.id.tripTime);
        no_of_passengers = findViewById(R.id.no_of_passengers);


        tripTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    setTime();
            }
        });
        tripTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!tripTime.getText().toString().isEmpty()) && (!tripDetail.getText().toString().isEmpty())
                        && (!tripName.getText().toString().isEmpty())
                        && (!no_of_passengers.getText().toString().isEmpty())) {
                    Date todayWithZeroTime = new Date();
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                    Date today = new Date();

                    try {
                       todayWithZeroTime = formatter.parse(formatter.format(today));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(CreateTripActivity.this, SelectVehicleActivity.class);
                    intent.putExtra("whichActivity", "CreateTrip");
                    intent.putExtra("tripName", tripName.getText().toString());
                    intent.putExtra("tripDetail", tripDetail.getText().toString());
                    intent.putExtra("tripDate", todayWithZeroTime.toString());
                    intent.putExtra("tripTime", tripTime.getText().toString());
                    intent.putExtra("no_of_passengers", no_of_passengers.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateTripActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void setTime(){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog();
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
        timePickerDialog.initialize(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                if (view.getSelectedTime().isAM())
                    if (view.getSelectedTime().getMinute()<10)
                        tripTime.setText(view.getSelectedTime().getHour()+":0"+view.getSelectedTime().getMinute()+" AM");
                    else
                        tripTime.setText(view.getSelectedTime().getHour()+":0"+view.getSelectedTime().getMinute()+" AM");


                else
                    if (view.getSelectedTime().getMinute()<10)
                        tripTime.setText(view.getSelectedTime().getHour()+":0"+view.getSelectedTime().getMinute()+" PM");
                    else
                        tripTime.setText(view.getSelectedTime().getHour()+":0"+view.getSelectedTime().getMinute()+" PM");
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), false);
        timePickerDialog.setOkColor(Color.WHITE);
        timePickerDialog.setCancelColor(Color.WHITE);

        timePickerDialog.show(getSupportFragmentManager(), "Timepickerdialog");
    }
}
