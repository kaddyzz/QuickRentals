package com.example.quickrentals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.quickrentals.Adapters.CarsAdapter;
import com.example.quickrentals.ModelClasses.Cars;
import com.example.quickrentals.Vendor.VendorBookingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class CarsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private CarsAdapter carsAdapter;
    private KProgressHUD kProgressHUD;
    private String startDate, endDate;
    private FirebaseFirestore db;
    private Spinner spinnerCarType;
    private String[] arrayCarType = {"All","Sports Car", "Sedan", "Hatchback", "SUV"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setTheme("@valuew");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        //Instantiations
        TextView textViewStartDate = findViewById(R.id.textViewStartDate);
        TextView textViewEndDate = findViewById(R.id.textViewEndDate);

        spinnerCarType = findViewById(R.id.spinnerCarType);
        spinnerCarType.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, arrayCarType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spinnerCarType.setAdapter(aa);


        //Get last data
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");

            textViewStartDate.setText(startDate);
            textViewEndDate.setText(endDate);

        }

        //Progress HUD
        kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();

        getCars(0);

    }

    private void getCars(int position)
    {
        kProgressHUD.show();


        if (position > 0)
        {
            //Get cars
            db.collection("cars")
                    .whereEqualTo("carType",spinnerCarType.getSelectedItem().toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            //Dismiss HUD
                            kProgressHUD.dismiss();

                            List<Cars> carsList = new ArrayList<>();

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("FIREBASE", document.getId() + " => " + document.get("carMake"));

                                    //Add each car to car list
                                    Cars cars = document.toObject(Cars.class);
                                    carsList.add(cars);
                                }

                                carsAdapter = new CarsAdapter(carsList, startDate, endDate);
                                recyclerView = findViewById(R.id.recyclerViewCars);
                                recyclerView.setLayoutManager(new LinearLayoutManager(CarsActivity.this));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(carsAdapter);

                            } else {
                                Log.d("FIREBASE", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else {
            //Get cars
            db.collection("cars")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            //Dismiss HUD
                            kProgressHUD.dismiss();

                            List<Cars> carsList = new ArrayList<>();

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("FIREBASE", document.getId() + " => " + document.get("carMake"));

                                    //Add each car to car list
                                    Cars cars = document.toObject(Cars.class);
                                    carsList.add(cars);
                                }

                                carsAdapter = new CarsAdapter(carsList, startDate, endDate);
                                recyclerView = findViewById(R.id.recyclerViewCars);
                                recyclerView.setLayoutManager(new LinearLayoutManager(CarsActivity.this));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(carsAdapter);

                            } else {
                                Log.d("FIREBASE", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //onItemSelected
        getCars(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}