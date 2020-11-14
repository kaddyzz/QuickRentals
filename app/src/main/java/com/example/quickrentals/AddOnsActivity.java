package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quickrentals.ModelClasses.Cars;

public class AddOnsActivity extends AppCompatActivity {

    private String stringSelectedAddOns = "";
    private int addOnPrice = 0;

    private CheckBox checkBoxAddionalKey;
    private CheckBox checkBoxChildSeat;
    private CheckBox checkBoxGloves;

    private Spinner spinnerLocation;

    private String startDate, endDate, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ons);



        //Get selected car ID from bundle
        Bundle bundle = getIntent().getExtras();
        Cars selectedCar = new Cars();


        if(bundle!=null)
        {
            selectedCar = (Cars) bundle.getSerializable("selectedCar");
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");
        }

        //Create a variable for data send
        final Cars finalSelectedCar = selectedCar;

        Button buttonFinalReview = findViewById(R.id.buttonConfirmReview);

        checkBoxAddionalKey = findViewById(R.id.checkBoxAddionalKey);
        checkBoxChildSeat = findViewById(R.id.checkBoxChildSeat);
        checkBoxGloves = findViewById(R.id.checkBoxGloves);
        spinnerLocation = findViewById(R.id.spinnerPickupLocations);



        buttonFinalReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start new activity
                calcAddOns();

                Intent moveWithData = new Intent( AddOnsActivity.this, ReviewActivity.class);

                moveWithData.putExtra("selectedCar", finalSelectedCar);
                moveWithData.putExtra("stringSelectedAddOns", stringSelectedAddOns);
                moveWithData.putExtra("addOnPrice", addOnPrice);
                moveWithData.putExtra("startDate",startDate);
                moveWithData.putExtra("endDate",endDate);
                moveWithData.putExtra("selectedLocation",location);

                startActivity(moveWithData);
            }
        });
    }

    private void calcAddOns()
    {

        //Save the location
        location = spinnerLocation.getSelectedItem().toString();

        if (checkBoxAddionalKey.isChecked() || checkBoxChildSeat.isChecked() || checkBoxGloves.isChecked())
        {
            if (checkBoxAddionalKey.isChecked())
            {
                stringSelectedAddOns += "Additional Key";
                addOnPrice += 10;
            }
            if (checkBoxChildSeat.isChecked())
            {
                stringSelectedAddOns += "| Child Seat";
                addOnPrice += 70;
            }
            if (checkBoxGloves.isChecked())
            {
                stringSelectedAddOns += "| Gloves";
                addOnPrice += 10;
            }
        }
        else
        {
            stringSelectedAddOns = "None";
            addOnPrice = 0;
        }

    }



}