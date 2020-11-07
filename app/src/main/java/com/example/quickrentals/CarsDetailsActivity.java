package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CarsDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_details);

        Button buttonGoToAddOns = findViewById(R.id.buttonGoToAddOns);

        buttonGoToAddOns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CarsDetailsActivity.this, AddOnsActivity.class));
            }
        });
    }
}