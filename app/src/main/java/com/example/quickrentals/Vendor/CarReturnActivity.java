package com.example.quickrentals.Vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quickrentals.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

public class CarReturnActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String bookingID;
    private String bookingAddons;
    private String fuelPercent;

    private CheckBox checkBoxFuelIndex;
    private CheckBox checkBoxNoDamage;
    private CheckBox checkBoxAddOns;

    private Spinner spinnerRating;
    private EditText editTextFeedback;

    private String[] spinnerRatingArray = {"1","2","3","4","5"};
    private KProgressHUD hud;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_return);

        //Get selected car ID from bundle
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            bookingID = bundle.getString("bookingID");
            bookingAddons = bundle.getString("addOns");
            fuelPercent = bundle.getString("fuelPercent");
        }

        checkBoxFuelIndex = findViewById(R.id.checkBoxFuelIndex);
        checkBoxNoDamage = findViewById(R.id.checkBoxNoDamage);
        checkBoxAddOns = findViewById(R.id.checkBoxAddOns);

        spinnerRating = findViewById(R.id.spinnerRating);
        editTextFeedback = findViewById(R.id.editTextFeedback);
        Button buttonCarReturn = findViewById(R.id.buttonCarReturn);

        spinnerRating.setOnItemSelectedListener(CarReturnActivity.this);

        checkBoxFuelIndex.setText(String.format("Fuel index %s",fuelPercent));

        if (bookingAddons.equals("None"))
        {
            checkBoxAddOns.setText(R.string.noAddons);
        }
        else
        {
            checkBoxAddOns.setText(String.format("Collected %s back.",bookingAddons));
        }

        //Creating the ArrayAdapter instance having the rating list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerRatingArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Setting the ArrayAdapter data on the Spinner
        spinnerRating.setAdapter(aa);

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();

        //Create HUD
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");

        buttonCarReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEverythingAndProcessReturn();
            }
        });
    }

    private void checkEverythingAndProcessReturn()
    {
        if ((!checkBoxFuelIndex.isChecked() || !checkBoxNoDamage.isChecked() || !checkBoxAddOns.isChecked()) && editTextFeedback.getText().toString().equals(""))
        {
            Toast.makeText(this, "You have to enter the details if something is unchecked.", Toast.LENGTH_SHORT).show();
        }
        else if (spinnerRating.getSelectedItem().toString().equals("0"))
        {
            Toast.makeText(this, "Please enter feedback.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            processReturn();
        }
    }

    private void processReturn() {

        hud.show();

        String rating = spinnerRating.getSelectedItem().toString();


        db.collection("bookings").document(bookingID)
                .update("bookingStatus", "2","userRating",rating,"bookingVendorDamageFeedback",editTextFeedback.getText().toString().trim())
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
                        Toast.makeText(CarReturnActivity.this, "There is a problem with the return.", Toast.LENGTH_SHORT).show();
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