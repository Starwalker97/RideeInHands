package com.example.rideeinhands;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.TimeZone;

public class GetARideActivity extends AppCompatActivity {
    MaterialEditText tripDate, tripTime, no_of_passengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_aride);

        tripDate = findViewById(R.id.tripDate);
        tripTime = findViewById(R.id.tripTime);
        no_of_passengers = findViewById(R.id.no_of_passengers);

        tripDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    setDate();
            }
        });
        tripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

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
                Intent intent = new Intent(GetARideActivity.this, SelectLocation.class);
                intent.putExtra("whichActivity", "GetARide");
                intent.putExtra("tripDate", tripDate.getText().toString());
                intent.putExtra("tripTime", tripTime.getText().toString());
                intent.putExtra("no_of_passengers", no_of_passengers.getText().toString());
                startActivity(intent);
            }
        });

    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog();
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);
        timePickerDialog.initialize(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                if (view.getSelectedTime().isAM())
                    if (view.getSelectedTime().getMinute() < 10)
                        tripTime.setText(view.getSelectedTime().getHour() + ":0" + view.getSelectedTime().getMinute() + " AM");
                    else
                        tripTime.setText(view.getSelectedTime().getHour() + ":0" + view.getSelectedTime().getMinute() + " AM");


                else if (view.getSelectedTime().getMinute() < 10)
                    tripTime.setText(view.getSelectedTime().getHour() + ":0" + view.getSelectedTime().getMinute() + " PM");
                else
                    tripTime.setText(view.getSelectedTime().getHour() + ":0" + view.getSelectedTime().getMinute() + " PM");
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), false);
        timePickerDialog.setOkColor(Color.WHITE);
        timePickerDialog.setCancelColor(Color.WHITE);

        timePickerDialog.show(getSupportFragmentManager(), "Timepickerdialog");
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        long today = MaterialDatePicker.todayInUtcMilliseconds();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select the trip date");
        builder.setSelection(today);
        builder.setCalendarConstraints(constraintsBuilder.build());
        final MaterialDatePicker materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");


        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                tripDate.setText(materialDatePicker.getHeaderText());
            }
        });

    }
}
