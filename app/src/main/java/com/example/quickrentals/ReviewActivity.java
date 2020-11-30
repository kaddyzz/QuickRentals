package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickrentals.ModelClasses.Cars;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewActivity extends AppCompatActivity {

    private String startDate, endDate, location, dayDifference;
    private Cars selectedCar;
    private String stringAddOns;
    private int addOnsPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //Get selected car ID from bundle
        Bundle bundle = getIntent().getExtras();
        selectedCar = new Cars();
        stringAddOns = "";
        addOnsPrice = 0;

        if(bundle!=null)
        {
            selectedCar = (Cars) bundle.getSerializable("selectedCar");
            stringAddOns = bundle.getString("stringSelectedAddOns");
            addOnsPrice = bundle.getInt("addOnPrice");
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");
            location = bundle.getString("selectedLocation");
        }

        //Instantiation


        TextView textViewCarName = findViewById(R.id.textViewCarName);
        TextView textViewDate = findViewById(R.id.textViewStart);
        TextView textViewAddOns = findViewById(R.id.textViewAddOns);
        TextView textViewLocation = findViewById(R.id.textViewLocation);

        //Headings alter
        TextView textViewTitleDates = findViewById(R.id.textViewTitleDate);
        TextView textViewTitleAddOns = findViewById(R.id.textViewTitleAddOns);
        TextView textViewCarTitle = findViewById(R.id.textViewCarTitle);

        Button buttonConfirmReview = findViewById(R.id.buttonConfirmReview);




        buttonConfirmReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start payment activity
                Intent moveWithData = new Intent(ReviewActivity.this, PaymentCarActivity.class);

                moveWithData.putExtra("selectedCar", selectedCar);
                moveWithData.putExtra("stringSelectedAddOns", stringAddOns);
                moveWithData.putExtra("addOnPrice", addOnsPrice);
                moveWithData.putExtra("startDate",startDate);
                moveWithData.putExtra("endDate",endDate);
                moveWithData.putExtra("selectedLocation",location);
                moveWithData.putExtra("dayDifference",dayDifference);

                startActivity(moveWithData);
            }
        });

        textViewCarName.setText(String.format("%s %s",selectedCar.getCarMake(),selectedCar.getCarModel()));
        textViewDate.setText(String.format("%s - %s",startDate,endDate));
        textViewAddOns.setText(stringAddOns);
        textViewLocation.setText(location);

        //Test
        try {
            Date dateStart;
            Date dateReturn;
            SimpleDateFormat dates = new SimpleDateFormat("MM/dd/yyyy");
            dateStart = dates.parse(startDate);
            dateReturn = dates.parse(endDate);
            long difference = Math.abs(dateStart.getTime() - dateReturn.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            dayDifference = Long.toString(differenceDates);


            //Set headings
            textViewTitleDates.setText(String.format("Selected Dates ( %s day(s))",dayDifference));
            textViewCarTitle.setText(String.format("Car ($%s X %s = $%s)",selectedCar.getCarPrice(),dayDifference,Integer.parseInt(selectedCar.getCarPrice()) * Integer.parseInt(dayDifference)));
            textViewTitleAddOns.setText(String.format("Selected Add Ons ($%s)",addOnsPrice));

        } catch (Exception exception) {
            Toast.makeText(this, "Unable to find difference", Toast.LENGTH_SHORT).show();
        }


    }


}