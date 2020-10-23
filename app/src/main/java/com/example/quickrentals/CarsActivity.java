package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class CarsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CarsAdapter carsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        //Create modal class object
        final List<Cars> carsList = new ArrayList<>();

        carsAdapter = new CarsAdapter(carsList);
        recyclerView = findViewById(R.id.recyclerViewCars);
        recyclerView.setLayoutManager(new LinearLayoutManager(CarsActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(carsAdapter);
    }
}