package com.example.quickrentals.Vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quickrentals.BookingDetailsActivity;
import com.example.quickrentals.ModelClasses.Cars;
import com.example.quickrentals.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

public class CarIssueActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private LottieAnimationView toggle;
    private int flag = 0;
    private String[] spinnerFuelBrackets = { "0%", "10%", "20%", "30%", "40%","50%","60%","70%","80%","90%","100%"};
    private KProgressHUD hud;
    private FirebaseFirestore db;
    private Spinner spinnerFuelBracket;

    private String bookingID;
    private String bookingUserName;
    private String bookingAddons;

    private EditText editTextDL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_issue);

        //Get selected car ID from bundle
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            bookingID = bundle.getString("bookingID");
            bookingUserName = bundle.getString("userName");
            bookingAddons = bundle.getString("addOns");
        }

        toggle = findViewById(R.id.lav_toggle);
        TextView textViewTitleDL = findViewById(R.id.textViewTitleDL);
        editTextDL = findViewById(R.id.editTextDL);
        Button buttonTermsAndConditions = findViewById(R.id.buttonTermsAndConditions);
        Button buttonCarIssue = findViewById(R.id.buttonCarIssue);
        spinnerFuelBracket = findViewById(R.id.spinnerFuelLevel);

        spinnerFuelBracket.setOnItemSelectedListener(CarIssueActivity.this);
        textViewTitleDL.setText(String.format("Please enter DL Number of %s.",bookingUserName));

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerFuelBrackets);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spinnerFuelBracket.setAdapter(aa);

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();

        //Create HUD
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeState();
            }
        });

        buttonCarIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEverythingAndIssueCar();
            }
        });

        buttonTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarIssueActivity.this, TermsAndConditionActivity.class));
            }
        });
    }

    private void changeState() {
        if (flag == 0) {
            toggle.setMinAndMaxProgress(0f, 0.43f); //Here, calculation is done on the basis of start and stop frame divided by the total number of frames
            toggle.playAnimation();
            flag = 1;
            //---- Your code here------
        } else {
            toggle.setMinAndMaxProgress(0.5f, 1f);
            toggle.playAnimation();
            flag = 0;
            //---- Your code here------
        }
    }

    private void checkEverythingAndIssueCar()
    {
        if (editTextDL.getText().toString().trim().equals(""))
        {
            Toast.makeText(this, "Please enter the DL number.", Toast.LENGTH_SHORT).show();
        }
        else if (flag == 0)
        {
            Toast.makeText(this, "Please read terms & conditions.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            issueCar();
        }
    }

    private void issueCar()
    {
        hud.show();

        String fuelPercent = spinnerFuelBracket.getSelectedItem().toString();


        db.collection("bookings").document(bookingID)
                .update("bookingStatus", "1","fuelLevel",fuelPercent,"dlNumber",editTextDL.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        hud.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hud.dismiss();
                        Toast.makeText(CarIssueActivity.this, "There is a problem with the issue.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}