package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CarsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CarsAdapter carsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        String startDate, endDate;

        //Instantiations
        TextView textViewDate = findViewById(R.id.textViewDateReview);


        //Create modal class object
        final List<Cars> carsList = new ArrayList<>();



        //Get last data
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");

            String tempDateReview = "From " + startDate + " to " + endDate;
            textViewDate.setText(tempDateReview);
        }

        carsAdapter = new CarsAdapter(carsList);
        recyclerView = findViewById(R.id.recyclerViewCars);
        recyclerView.setLayoutManager(new LinearLayoutManager(CarsActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(carsAdapter);
    }
}