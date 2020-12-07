package com.example.quickrentals.NavigationFragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickrentals.Adapters.BookingsAdapter;
import com.example.quickrentals.CarsActivity;
import com.example.quickrentals.ModelClasses.Booking;
import com.example.quickrentals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomeActivity extends Fragment implements View.OnClickListener {

    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText editTextPickupDate , editTextReturnDate;
    Button buttonSelectCar;

    String userRating;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_welcome, container, false);


        //Object inst
        editTextPickupDate = view.findViewById(R.id.editTextPickupDate);
        editTextReturnDate = view.findViewById(R.id.editTextReturnDate);
        buttonSelectCar = view.findViewById(R.id.buttonSelectCar);
        TextView textViewSub = view.findViewById(R.id.textViewSub);
        TextView textViewSalutation = view.findViewById(R.id.textViewWishes);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            textViewSalutation.setText("Good Morning");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            textViewSalutation.setText("Good Afternoon");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            textViewSalutation.setText("Good Evening");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            textViewSalutation.setText("Good Night");
        }

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

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);

                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                            String strDatePickup = format.format(calendar.getTime());

                            editTextPickupDate.setText(strDatePickup);

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
            Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

            c.setTime(tomorrow);

            long minDate = c.getTime().getTime();// Twice!

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);

                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                            String strDateReturn = format.format(calendar.getTime());

                            editTextReturnDate.setText(strDateReturn);

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
                //Check if the dates are valid.
                try {
                    Date dateStart;
                    Date dateReturn;
                    SimpleDateFormat dates = new SimpleDateFormat("MM/dd/yyyy");
                    dateStart = dates.parse(editTextPickupDate.getText().toString());
                    dateReturn = dates.parse(editTextReturnDate.getText().toString());

                    long difference = dateStart.getTime() - dateReturn.getTime();

                    if (difference > 0)
                    {
                        Toast.makeText(getActivity(), "Please select a valid return date!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(getActivity(), CarsActivity.class);

                        intent.putExtra("startDate",editTextPickupDate.getText().toString());
                        intent.putExtra("endDate",editTextReturnDate.getText().toString());

                        startActivity(intent);
                    }

                } catch (Exception exception) {
                    Toast.makeText(view.getContext(), "Unable to find difference", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static int getDaysDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null)
            return 0;

        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

}