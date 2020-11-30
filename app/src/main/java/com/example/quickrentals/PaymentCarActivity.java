package com.example.quickrentals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickrentals.ModelClasses.Booking;
import com.example.quickrentals.ModelClasses.Cars;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentCarActivity extends AppCompatActivity {

    private PayPalConfiguration config;
    private KProgressHUD hud;
    private Cars selectedCar;
    private Booking booking;

    private FirebaseFirestore db;

    private double finalCarPrice = 0;
    private double finalCarPriceWithAddOns = 0;
    private double pricePVRT = 0;
    private double priceVLF_REC = 0;
    private double goodAndServiceTax = 0;
    private double PST = 0;
    private double finalCarPriceWithTax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_car);

        TextView textViewTitleCarPrice = findViewById(R.id.textViewTitleCarPrice);
        TextView textViewCarPrice = findViewById(R.id.textViewCarPrice);

        TextView textViewTitleAddOns = findViewById(R.id.textViewTitleAddOns);
        TextView textViewAddOns = findViewById(R.id.textViewAddOns);

        TextView textViewGST = findViewById(R.id.textViewGST);
        TextView textViewPST = findViewById(R.id.textViewPST);
        TextView textViewPVRT = findViewById(R.id.textViewPVRT);
        TextView textViewTitleVLC = findViewById(R.id.textViewVLC);

        TextView textViewFinalPrice = findViewById(R.id.textViewFinalPrice);

        TextView textViewPickupPrice = findViewById(R.id.textViewPickupPrice);
        TextView textViewPaypalPrice = findViewById(R.id.textViewPaypalPrice);

        Button buttonInformationTaxes = findViewById(R.id.buttonInformationTaxes);

        buttonInformationTaxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentCarActivity.this, TaxexScrollingActivity.class));
            }
        });


        //Get last data
        Bundle bundle = getIntent().getExtras();
        booking = new Booking();

        if(bundle!=null)
        {
            selectedCar = (Cars) bundle.getSerializable("selectedCar");

            booking.setAddOns(bundle.getString("stringSelectedAddOns"));
            booking.setAddOnsPrice(String.format("%s",bundle.getInt("addOnPrice")));
            booking.setPickUpDate(bundle.getString("startDate"));
            booking.setReturnDate(bundle.getString("endDate"));
            booking.setSelectedLocation(bundle.getString("selectedLocation"));
            booking.setNoOfDays(bundle.getString("dayDifference"));

            //Fill in other details
            booking.setCarImage(selectedCar.getCarImage());
            booking.setCarMake(selectedCar.getCarMake());
            booking.setCarModel(selectedCar.getCarModel());
            booking.setCarPricePerDay(selectedCar.getCarPrice());


            finalCarPrice = (Integer.parseInt(selectedCar.getCarPrice()) * Integer.parseInt(booking.getNoOfDays()));

            finalCarPriceWithAddOns = (Integer.parseInt(selectedCar.getCarPrice()) * Integer.parseInt(booking.getNoOfDays())) + Integer.parseInt(booking.getAddOnsPrice());

            pricePVRT = 1.50 * Double.parseDouble(booking.getNoOfDays());
            priceVLF_REC = 1.07 * Double.parseDouble(booking.getNoOfDays());
            goodAndServiceTax = 0.05 * finalCarPriceWithAddOns;
            PST = 0.07 * finalCarPriceWithAddOns;

            finalCarPriceWithTax = finalCarPriceWithAddOns + pricePVRT + priceVLF_REC + goodAndServiceTax + PST;

            booking.setFinalPrice(String.format("%.2f",finalCarPriceWithTax));
        }

        //Set values in recipt
        textViewTitleCarPrice.setText(String.format("%s %s for %s day(s)",selectedCar.getCarMake(),selectedCar.getCarModel(), booking.getNoOfDays()));
        textViewCarPrice.setText(String.format("CAD %s",finalCarPrice));

        textViewTitleAddOns.setText(booking.getAddOns());
        textViewAddOns.setText(String.format("CAD %s",booking.getAddOnsPrice()));

        textViewGST.setText(String.format("CAD %.2f",goodAndServiceTax));
        textViewPST.setText(String.format("CAD %.2f",PST));
        textViewPVRT.setText(String.format("CAD %.2f",pricePVRT));
        textViewTitleVLC.setText(String.format("CAD %.2f",priceVLF_REC));

        textViewFinalPrice.setText(String.format("CAD %.2f",finalCarPriceWithTax));

        double finalPriceWithDiscount = finalCarPriceWithTax - (finalCarPriceWithTax * 0.02);

        textViewPaypalPrice.setText(String.format("CAD %.2f", finalPriceWithDiscount));
        textViewPickupPrice.setText(String.format("CAD %.2f", finalCarPriceWithTax));

        //Button
        Button buttonPayPaypal = findViewById(R.id.buttonPayPaypal);
        Button buttonPayOnDelivery = findViewById(R.id.buttonPayAtPickup);

        buttonPayOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizeBooking("Payment Pending");
            }
        });

        buttonPayPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

        db = FirebaseFirestore.getInstance();

        config = new PayPalConfiguration()

                // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                // or live (ENVIRONMENT_PRODUCTION)
                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                .clientId("AWaTo750MrZSX7MiTra3u4gFHn4r_h1ScW68YX2c4fAKnU3l96sKYOHmtzZxJTfzFaTcyPGgCBBe0FVA");

        //Progress HUD
        hud = KProgressHUD.create(PaymentCarActivity.this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Processing");
    }


    public void processPayment() {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(finalCarPriceWithTax)), "CAD", "Quick Rentals Booking",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    //Add into firebase
                    finalizeBooking("Payment Complete");

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }


    public void finalizeBooking(final String paymentMethod)
    {
        //Get id
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        //Finalize booking details
        booking.setPaymentStatus(paymentMethod);
        booking.setUserName(pref.getString("fullName", ""));
        booking.setUserID(pref.getString("userID", ""));
        booking.setBookingStatus("0");

        hud.show();

        // Add a new document for new order
        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        //Dismiss HUD
                        hud.dismiss();

                        Intent moveWithData = new Intent( PaymentCarActivity.this, ConfirmationActivity.class);

                        moveWithData.putExtra("paymentMethod", booking.getPaymentStatus());
                        moveWithData.putExtra("finalPriceWithTex", booking.getFinalPrice());
                        moveWithData.putExtra("startDate",booking.getPickUpDate());
                        moveWithData.putExtra("endDate",booking.getReturnDate());
                        moveWithData.putExtra("selectedLocation",booking.getSelectedLocation());

                        startActivity(moveWithData);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase: ", "Error adding user", e);
                    }
                });


    }
}