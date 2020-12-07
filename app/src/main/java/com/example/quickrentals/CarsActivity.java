package com.example.quickrentals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickrentals.Adapters.CarsAdapter;
import com.example.quickrentals.ModelClasses.Booking;
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
    private float userRating;


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

        //Get rating and then get cars
        getRating();

    }

    private void getCars(int position)
    {
        kProgressHUD.show();

        //SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        //This algorithm will increase the price of cars according to user rating for per day.
        final float ratingAdder = 100 - (userRating * 20);

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

                                    float updatedCardPrice = Float.parseFloat(cars.getCarPrice()) + ratingAdder;

                                    cars.setCarPrice(String.format("%.2f",updatedCardPrice));

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

                                    float updatedCardPrice = Float.parseFloat(cars.getCarPrice()) + ratingAdder;

                                    cars.setCarPrice(String.format("%.2f",updatedCardPrice));

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

    private void getRating() {

        // [START get_firestore_instance]
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Create HUD
        kProgressHUD.show();

        //Get id
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        //Get bookings from firebase
        db.collection("bookings")
                .whereEqualTo("userID", pref.getString("userID", ""))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //Dismiss HUD
                        kProgressHUD.dismiss();

                        float counter = 0;
                        float fullRating = 0;


                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //Add each car to car list
                                Booking booking = document.toObject(Booking.class);

                                if (booking.getBookingStatus().equals("2")) {
                                    counter++;

                                    fullRating += Float.parseFloat(booking.getUserRating());
                                }
                            }

                            if (fullRating == 0)
                            {
                                userRating = 5.0f;
                            }
                            else
                            {
                                userRating = fullRating/counter;
                            }

                            getCars(0);


                        } else {
                            Log.d("FIREBASE", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}