package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quickrentals.ModelClasses.Cars;

public class ReviewActivity extends AppCompatActivity {

    private String startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //Get selected car ID from bundle
        Bundle bundle = getIntent().getExtras();
        Cars selectedCar = new Cars();
        String stringAddOns = "";
        int addOnsPrice = 0;

        if(bundle!=null)
        {
            selectedCar = (Cars) bundle.getSerializable("selectedCar");
            stringAddOns = bundle.getString("stringSelectedAddOns");
            addOnsPrice = bundle.getInt("addOnPrice");
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");
        }

        //Instantiation
        TextView textViewCarName = findViewById(R.id.textViewCarName);
        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView textViewAddOns = findViewById(R.id.textViewAddOns);
        TextView textViewLocation = findViewById(R.id.textViewLocation);

        Button buttonConfirmReview = findViewById(R.id.buttonConfirmReview);

        buttonConfirmReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start payment activity
                startActivity(new Intent(ReviewActivity.this, PaymentCarActivity.class));
            }
        });

        textViewCarName.setText(String.format("%s %s",selectedCar.getCarMake(),selectedCar.getCarModel()));
        textViewDate.setText(String.format("%s - %s",startDate,endDate));
        textViewAddOns.setText(stringAddOns);
        textViewLocation.setText("Surrey");

    }
}