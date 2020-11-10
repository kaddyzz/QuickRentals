package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.quickrentals.ModelClasses.Cars;

public class AddOnsActivity extends AppCompatActivity {

    private String stringSelectedAddOns = "";
    private int addOnPrice = 0;

    private CheckBox checkBoxAddionalKey;
    private CheckBox checkBoxChildSeat;
    private CheckBox checkBoxGloves;

    private String startDate, endDate;

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

                startActivity(moveWithData);
            }
        });
    }

    private void calcAddOns()
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

}