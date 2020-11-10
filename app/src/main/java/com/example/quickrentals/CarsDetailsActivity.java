package com.example.quickrentals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quickrentals.ModelClasses.Cars;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.time.LocalDate;

public class CarsDetailsActivity extends AppCompatActivity {

    private KProgressHUD kProgressHUD;
    private FirebaseFirestore db;
    private String startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_details);

        // [START get_firestore_instance]
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Progress HUD
        kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");

        String carID = "";

        Button buttonGoToAddOns = findViewById(R.id.buttonGoToAddOns);

        final TextView textViewCarMake = findViewById(R.id.textViewCarMake);
        final TextView textViewCarModel = findViewById(R.id.textViewCarModel);
        final TextView textViewSeats = findViewById(R.id.textViewSeats);
        final TextView textViewHP = findViewById(R.id.textViewHP);
        final TextView textViewCylinder = findViewById(R.id.textViewCylinder);
        final TextView textViewCarType = findViewById(R.id.textViewCarType);
        final TextView textViewFuel = findViewById(R.id.textViewFuel);
        final TextView textViewSpeed = findViewById(R.id.textViewSpeed);
        final TextView textViewCarPrice = findViewById(R.id.textViewCarPrice);

        final ImageView imageViewCar = findViewById(R.id.imageViewCarImage);
        final ImageView imageViewCarLogo = findViewById(R.id.imageViewLogo);

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

        buttonGoToAddOns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent moveWithData = new Intent( CarsDetailsActivity.this, AddOnsActivity.class);

                moveWithData.putExtra("selectedCar", finalSelectedCar);
                moveWithData.putExtra("startDate",startDate);
                moveWithData.putExtra("endDate",endDate);

                startActivity(moveWithData);
            }
        });

        Glide.with(CarsDetailsActivity.this).load(selectedCar.getCarImage()).into(imageViewCar);
        Glide.with(CarsDetailsActivity.this).load(selectedCar.getCarLogo()).into(imageViewCarLogo);


        textViewCarMake.setText(selectedCar.getCarMake());
        textViewCarModel.setText(selectedCar.getCarModel());

        textViewCarPrice.setText(String.format("$ %s per day",selectedCar.getCarPrice()));

        textViewSeats.setText(selectedCar.getCarSeats());
        textViewSpeed.setText(selectedCar.getCarSpeed());
        textViewFuel.setText(selectedCar.getCarFuelEconomy());
        textViewCylinder.setText(selectedCar.getCarCylinder());
        textViewCarType.setText(selectedCar.getCarType());
        textViewHP.setText(selectedCar.getCarHorsePower());


        /*LocalDate.of( 2010 , 1 , 20 )
                .isBefore(
                        LocalDate.of( 2014 , 2 , 30 )
                )*/


    }
}