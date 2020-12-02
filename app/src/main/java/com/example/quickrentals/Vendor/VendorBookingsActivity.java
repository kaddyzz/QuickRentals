package com.example.quickrentals.Vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickrentals.Adapters.BookingsAdapter;
import com.example.quickrentals.ModelClasses.Booking;
import com.example.quickrentals.NavigationFragments.NavigationMainActivity;
import com.example.quickrentals.PreAuth.FirstActivity;
import com.example.quickrentals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class VendorBookingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private BookingsAdapter bookingsAdapter;
    private RecyclerView recyclerViewBookings;
    private String location = "";
    private Spinner spinnerLocation;
    private String[] spinnerLocations = { "Surrey", "South Burnaby", "Metrotown", "Coquitlam", "Port Coquitlam","Newton","Vancouver","Richmond"};
    private KProgressHUD hud;
    private TextView textViewTitle;
    private Button buttonSignout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_bookings);

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();

        //Create HUD
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");


        textViewTitle = findViewById(R.id.textViewTitle);
        buttonSignout = findViewById(R.id.buttonSignOut);
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);


        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spinnerLocation = findViewById(R.id.spinnerLocations);
        spinnerLocation.setOnItemSelectedListener(VendorBookingsActivity.this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerLocations);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spinnerLocation.setAdapter(aa);

        buttonSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Sign out from Vendor
                startActivity(new Intent(VendorBookingsActivity.this, FirstActivity.class));

            }
        });

        getBookings();



    }

    private void getBookings()
    {
        hud.show();

        String location = spinnerLocation.getSelectedItem().toString();

        //Get bookings from firebase
        db.collection("bookings")
                .whereEqualTo("selectedLocation",location)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //Dismiss HUD
                        hud.dismiss();

                        String bookingID = "";

                        //Create modal class object
                        List<Booking> bookingList = new ArrayList<>();


                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //Add each car to car list
                                Booking booking = document.toObject(Booking.class);
                                booking.setUserID(document.getId());
                                bookingList.add(booking);
                            }


                            if (bookingList.isEmpty())
                            {
                                recyclerViewBookings.setVisibility(View.INVISIBLE);
                                textViewTitle.setText("You haven't booked any cars yet!");
                            }
                            else
                            {
                                recyclerViewBookings.setVisibility(View.VISIBLE);

                                bookingsAdapter = new BookingsAdapter(bookingList, true);

                                recyclerViewBookings.setLayoutManager(new LinearLayoutManager(VendorBookingsActivity.this));

                                recyclerViewBookings.setItemAnimator(new DefaultItemAnimator());

                                recyclerViewBookings.setAdapter(bookingsAdapter);
                            }
                        } else {
                            Log.d("FIREBASE", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        getBookings();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getBookings();
    }
}