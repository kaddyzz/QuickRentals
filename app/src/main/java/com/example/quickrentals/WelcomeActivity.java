package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText editTextPickupDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Object inst
        editTextPickupDate = findViewById(R.id.editTextPickupDate);
        EditText editTextReturnDate = findViewById(R.id.editTextReturnDate);

        editTextPickupDate.setOnClickListener(this);

        //




    }


    @Override
    public void onClick(View view) {

        if (view == editTextPickupDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            Date today = new Date();
            c.setTime(today);
            long minDate = c.getTime().getTime();// Twice!


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            editTextPickupDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.getDatePicker().setMinDate(minDate);
            datePickerDialog.show();
        }

    }
}