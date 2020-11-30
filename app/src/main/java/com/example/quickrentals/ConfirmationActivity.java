package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quickrentals.NavigationFragments.NavigationMainActivity;

import java.util.Calendar;
import java.util.Locale;

public class ConfirmationActivity extends AppCompatActivity {

    private String paymentMethod, finalPrice, startDate, endDate, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        //Get last data
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            paymentMethod = bundle.getString("paymentMethod");
            finalPrice = bundle.getString("finalPriceWithTex");
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");
            location = bundle.getString("selectedLocation");
        }

        Button buttonGoToHome = findViewById(R.id.buttonGoToHome);
        Button buttonAddToCalender = findViewById(R.id.buttonAddToCalender);
        Button buttonLocation = findViewById(R.id.buttonLocation);

        TextView textViewBookingConfirmed = findViewById(R.id.textViewBookingConfirmed);

        buttonLocation.setText(location);
        buttonAddToCalender.setText(String.format("%s - %s",startDate,endDate));

        if (paymentMethod.equals("Payment Pending"))
        {
            textViewBookingConfirmed.setText(String.format("Booking Confirmed \n(Pay $%s at the time of pickup.)",finalPrice));
        }
        else
        {
            textViewBookingConfirmed.setText(String.format("Booking Confirmed (Payment of $%s done.)",finalPrice));
        }

        buttonGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go Back to home
                startActivity(new Intent(ConfirmationActivity.this, NavigationMainActivity.class));
            }
        });

        buttonAddToCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", true);
                intent.putExtra("rule", "FREQ=YEARLY");
                intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                intent.putExtra("title", "Quick Rentals Reservation");

                startActivity(intent);
            }
        });

        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open location on maps,
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 49.190051,-122.847787);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }
}