package com.example.quickrentals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quickrentals.Adapters.BookingsAdapter;
import com.example.quickrentals.ModelClasses.Booking;
import com.example.quickrentals.ModelClasses.Cars;
import com.example.quickrentals.NavigationFragments.NavigationMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

public class BookingDetailsActivity extends AppCompatActivity {

    private Booking eachBooking;

    private TextView textViewCarMakeModel;
    private TextView textViewBookingID;
    private TextView textViewStartDate;
    private TextView textViewReturnDate;
    private TextView textViewPrice;
    private TextView textViewBookingStatus;

    private ImageView imageViewPaymentStatus;
    private ImageView imageViewCarImage;

    private View viewStatus;

    private boolean isVendor;

    private FirebaseFirestore db;

    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        //Getto
        eachBooking = new Booking();

        TextView textViewTitle = findViewById(R.id.textViewTitle);

        //Get last data
        Bundle bundle = getIntent().getExtras();

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();

        if(bundle!=null)
        {
            eachBooking = (Booking) bundle.getSerializable("booking");
            isVendor = bundle.getBoolean("isVendor");
        }

        //Lock
        textViewCarMakeModel = findViewById(R.id.textViewCarMakeModel);
        textViewBookingID = findViewById(R.id.textViewBookingID);
        textViewStartDate = findViewById(R.id.textViewStart);
        textViewReturnDate = findViewById(R.id.textViewReturnDate);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewBookingStatus = findViewById(R.id.textViewBookingStatus);

        imageViewPaymentStatus = findViewById(R.id.imageViewPaymentStatus);
        imageViewCarImage = findViewById(R.id.imageViewCarImage);

        viewStatus = findViewById(R.id.viewStatus);

        //
        textViewBookingID.setText(eachBooking.getUserName());
        textViewCarMakeModel.setText(String.format("%s %s",eachBooking.getCarMake(), eachBooking.getCarModel()));
        textViewTitle.setText(String.format("#%s",eachBooking.getUserID().substring(0,6)));
        textViewStartDate.setText(eachBooking.getPickUpDate());
        textViewReturnDate.setText(eachBooking.getReturnDate());
        textViewPrice.setText(String.format("$%s",eachBooking.getFinalPrice()));

        Glide.with(this).load(eachBooking.getCarImage()).into(imageViewCarImage);

        //Create HUD
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");

        if (eachBooking.getPaymentStatus().equals("Payment Pending"))
        {
            imageViewPaymentStatus.setImageResource(R.drawable.paymentailed);
        }
        else
        {
            imageViewPaymentStatus.setImageResource(R.drawable.p1);
        }

        if (isVendor)
        {
            if (eachBooking.getBookingStatus().equals("0") && eachBooking.getPaymentStatus().equals("Payment Pending"))
            {
                textViewBookingStatus.setText(R.string.upcoming);
                viewStatus.setBackgroundColor(0xFF03DAC5);
            }
            else if (eachBooking.getBookingStatus().equals("0") && eachBooking.getPaymentStatus().equals("Payment Complete"))
            {
                textViewBookingStatus.setText(R.string.upcoming);
                viewStatus.setBackgroundColor(0xFF03DAC5);
            }
            else if (eachBooking.getBookingStatus().equals("1"))
            {
                textViewBookingStatus.setText(R.string.ongoing);
                viewStatus.setBackgroundColor(0xFF4CAF50);
            }
            else
            {
                textViewBookingStatus.setText("");
                viewStatus.setBackgroundColor(0xFF9CA59C);
            }
        }
        else
        {
            if (eachBooking.getBookingStatus().equals("0"))
            {
                textViewBookingStatus.setText(R.string.upcoming);
                viewStatus.setBackgroundColor(0xFF03DAC5);
            }
            else if (eachBooking.getBookingStatus().equals("1"))
            {
                textViewBookingStatus.setText(R.string.ongoing);
                viewStatus.setBackgroundColor(0xFF4CAF50);
            }
            else
            {
                textViewBookingStatus.setText("");
                viewStatus.setBackgroundColor(0xFF9CA59C);
            }
        }

        Button buttonMulti = findViewById(R.id.buttonMulti);

        buttonMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailsActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                        cancelCar();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    private void cancelCar()
    {
        hud.show();

        //User ID is booking ID here
        db.collection("bookings").document(eachBooking.getUserID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BookingDetailsActivity.this, "There is a problem with the deletion.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}