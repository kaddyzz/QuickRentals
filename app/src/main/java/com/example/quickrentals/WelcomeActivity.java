package com.example.quickrentals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WelcomeActivity extends Fragment implements View.OnClickListener {

    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText editTextPickupDate , editTextReturnDate;
    Button buttonSelectCar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_welcome, container, false);

        //Object inst
        editTextPickupDate = view.findViewById(R.id.editTextPickupDate);
        editTextReturnDate = view.findViewById(R.id.editTextReturnDate);
        buttonSelectCar = view.findViewById(R.id.buttonSelectCar);
        TextView textViewSub = view.findViewById(R.id.textViewSub);

        //Call shared  pref to get data profile
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode

        textViewSub.setText(pref.getString("fullName", ""));

        editTextPickupDate.setOnClickListener(this);
        editTextReturnDate.setOnClickListener(this);
        buttonSelectCar.setOnClickListener(this);

        return view;

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

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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
        else if (view == editTextReturnDate) {

            //Validation for pickup date
            if (editTextPickupDate.getText().toString().isEmpty())
            {
                Toast.makeText(getActivity(), "Please select pickup date first.", Toast.LENGTH_SHORT).show();
                return;
            }

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            Date today = new Date();
            c.setTime(today);
            long minDate = c.getTime().getTime();// Twice!

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            editTextReturnDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.getDatePicker().setMinDate(minDate);
            datePickerDialog.show();
        }
        else if (view == buttonSelectCar)
        {
            if (editTextPickupDate.getText().toString().isEmpty() || editTextReturnDate.getText().toString().isEmpty())
            {
                Toast.makeText(getActivity(), "Please select booking dates first.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(getActivity(), CarsActivity.class);

                intent.putExtra("startDate",editTextPickupDate.getText().toString());
                intent.putExtra("endDate",editTextReturnDate.getText().toString());

                startActivity(intent);
            }

        }

    }

}