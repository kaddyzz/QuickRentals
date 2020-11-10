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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
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

    PayPalConfiguration config;
    KProgressHUD hud;

    Double carPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_car);

        //Get last data
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            //pizzaName = bundle.getString("pizzaName");
        }

        Button buttonPayPaypal = findViewById(R.id.buttonPayPaypal);
        Button buttonPayOnDelivery = findViewById(R.id.buttonPayAtPickup);

        buttonPayOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentCarActivity.this, ConfirmationActivity.class));
            }
        });

        buttonPayPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

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

        PayPalPayment payment = new PayPalPayment(new BigDecimal("14.00"), "CAD", "Quick Rentals Booking",
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
                    //finalizeBooking("Paypal");

                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

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

        //Get current date for order date
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String currentDate = df.format(Calendar.getInstance().getTime());

        //Get id
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        //hud.show();

        //Map all the values
        /*Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("userID",pref.getString("userID", ""));
        orderInfo.put("userPrice","$14");
        orderInfo.put("userPizzaName",pizzaName);
        orderInfo.put("userDate",currentDate);
        orderInfo.put("userAllergies", pizzaAllergies);
        orderInfo.put("userAddress", address);
        orderInfo.put("paymentMethod",paymentMethod);


        // Add a new document for new order
        db.collection("usersOrders")
                .add(orderInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        //Dismiss HUD
                        hud.dismiss();

                        Intent moveWithData = new Intent( PaymentActivity.this, OrderSuccessActivity.class);
                        moveWithData.putExtra("paymentMethod", paymentMethod);

                        startActivity(moveWithData);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firebase: ", "Error adding user", e);
                    }
                });

         */
    }
}