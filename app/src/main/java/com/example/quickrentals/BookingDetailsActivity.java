package com.example.quickrentals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.quickrentals.Adapters.BookingsAdapter;
import com.example.quickrentals.ModelClasses.Booking;
import com.example.quickrentals.Vendor.CarIssueActivity;
import com.example.quickrentals.Vendor.CarReturnActivity;
import com.example.quickrentals.Vendor.VendorBookingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class BookingDetailsActivity extends AppCompatActivity {

    private Booking eachBooking;

    private TextView textViewCarMakeModel;
    private TextView textViewBookingID;
    private TextView textViewStartDate;
    private TextView textViewReturnDate;
    private TextView textViewPrice;
    private TextView textViewBookingStatus;
    private TextView textViewTitle;

    private ImageView imageViewPaymentStatus;
    private ImageView imageViewCarImage;

    private CardView cardViewThumb;

    private View viewStatus;

    private Button buttonMulti;

    private boolean isVendor;

    private FirebaseFirestore db;

    private KProgressHUD hud;

    private LottieAnimationView thumb_up;
    private LottieAnimationView thumb_down;

    private String bookingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        //Getto
        eachBooking = new Booking();

        textViewTitle = findViewById(R.id.textViewWishes);

        //Get last data
        Bundle bundle = getIntent().getExtras();

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();

        if(bundle!=null)
        {
            bookingID = bundle.getString("bookingID");
            isVendor = bundle.getBoolean("isVendor");
        }

        //Lock
        textViewCarMakeModel = findViewById(R.id.textViewCarMakeModel);
        textViewBookingID = findViewById(R.id.textViewBookingID);
        textViewStartDate = findViewById(R.id.textViewStart);
        textViewReturnDate = findViewById(R.id.textViewReturnDate);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewBookingStatus = findViewById(R.id.textViewBookingStatus);

        buttonMulti = findViewById(R.id.buttonMulti);
        buttonMulti.setVisibility(View.VISIBLE);


        imageViewPaymentStatus = findViewById(R.id.imageViewPaymentStatus);
        imageViewCarImage = findViewById(R.id.imageViewCarImage);

        cardViewThumb = findViewById(R.id.cardViewThumbs);
        cardViewThumb.setVisibility(View.INVISIBLE);

        viewStatus = findViewById(R.id.viewStatus);

        thumb_up = findViewById(R.id.lav_thumbUp);
        thumb_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumb_down.setProgress(0);
                thumb_down.pauseAnimation();
                thumb_up.playAnimation();
                Toast.makeText(BookingDetailsActivity.this, "Cheers!!", Toast.LENGTH_SHORT).show();
                //---- Your code here------
                updateFeedback("1");

            }
        });

        thumb_down = findViewById(R.id.lav_thumbDown);
        thumb_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumb_up.setProgress(0);
                thumb_up.pauseAnimation();
                thumb_down.playAnimation();
                Toast.makeText(BookingDetailsActivity.this, "Boo!!", Toast.LENGTH_SHORT).show();
                //---- Your code here------
                updateFeedback("2");
            }
        });

        //Create HUD
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getBookings();
    }

    private void well()
    {
        textViewBookingID.setText(eachBooking.getUserName());
        textViewCarMakeModel.setText(String.format("%s %s",eachBooking.getCarMake(), eachBooking.getCarModel()));
        textViewTitle.setText(String.format("#%s",bookingID.substring(0,6)));
        textViewStartDate.setText(eachBooking.getPickUpDate());
        textViewReturnDate.setText(eachBooking.getReturnDate());
        textViewPrice.setText(String.format("$%s",eachBooking.getFinalPrice()));

        Glide.with(this).load(eachBooking.getCarImage()).into(imageViewCarImage);

        if (eachBooking.getPaymentStatus().equals("Payment Pending"))
        {
            imageViewPaymentStatus.setImageResource(R.drawable.paymentailed);
        }
        else
        {
            imageViewPaymentStatus.setImageResource(R.drawable.p1);
        }

        if (eachBooking.getBookingFeedback().equals("1"))
        {
            thumb_up.setProgress(1);
        }
        else if(eachBooking.getBookingFeedback().equals("2"))
        {
            thumb_down.setProgress(1);
        }

        if (isVendor)
        {
            if (eachBooking.getBookingStatus().equals("0") && eachBooking.getPaymentStatus().equals("Payment Pending"))
            {
                buttonMulti.setText("Take Payment");
                textViewBookingStatus.setText(R.string.upcoming);
                viewStatus.setBackgroundColor(0xFF03DAC5);

                buttonMulti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callAlert("Message!","Please use POS machine to take the payment of $" + eachBooking.getFinalPrice()+ "." ,"Complete","Cancel");
                    }
                });
            }
            else if (eachBooking.getBookingStatus().equals("0") && eachBooking.getPaymentStatus().equals("Payment Complete"))
            {
                textViewBookingStatus.setText(R.string.upcoming);
                viewStatus.setBackgroundColor(0xFF03DAC5);
                buttonMulti.setText("ISSUE CAR");


                buttonMulti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(BookingDetailsActivity.this, CarIssueActivity.class);

                        intent.putExtra("bookingID",bookingID);
                        intent.putExtra("userName",eachBooking.getUserName());

                        startActivity(intent);
                    }
                });
            }
            else if (eachBooking.getBookingStatus().equals("1"))
            {
                textViewBookingStatus.setText(R.string.ongoing);
                viewStatus.setBackgroundColor(0xFF4CAF50);
                buttonMulti.setText("PROCESS RETURN");
                buttonMulti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BookingDetailsActivity.this, CarReturnActivity.class);

                        intent.putExtra("bookingID",bookingID);
                        intent.putExtra("addOns",eachBooking.getAddOns());

                        startActivity(intent);
                    }
                });
            }
            else
            {
                textViewBookingStatus.setText(R.string.completedTrip);
                viewStatus.setBackgroundColor(0xFF9CA59C);
                buttonMulti.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            if (eachBooking.getBookingStatus().equals("0"))
            {
                buttonMulti.setText("Cancel");
                textViewBookingStatus.setText(R.string.upcoming);
                viewStatus.setBackgroundColor(0xFF03DAC5);

                buttonMulti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callAlert("Alert!","Are you sure?","YES","NO");
                    }
                });
            }
            else if (eachBooking.getBookingStatus().equals("1"))
            {
                buttonMulti.setText("Need RoadSide Assistance");
                textViewBookingStatus.setText(R.string.ongoing);
                viewStatus.setBackgroundColor(0xFF4CAF50);

                buttonMulti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callAlert("Message!","Please give us a call at +12345678909","CALL","CANCEL");
                    }
                });
            }
            else
            {
                cardViewThumb.setVisibility(View.VISIBLE);
                buttonMulti.setText("Share Receipt");
                textViewBookingStatus.setText(R.string.completedTrip);
                viewStatus.setBackgroundColor(0xFF9CA59C);

                buttonMulti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callAlert("Message!","Do you want to share the receipt of booking id " + textViewTitle.getText().toString() + "?","YES","NO");
                    }
                });
            }
        }
    }

    private void callAlert(String title, String message,String positive, String negative)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetailsActivity.this);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();
                doWhateverIsNeedful();
            }
        });

        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void doWhateverIsNeedful()
    {

        if (isVendor)
        {
            if (eachBooking.getBookingStatus().equals("0") && eachBooking.getPaymentStatus().equals("Payment Pending"))
            {
                updatePayment();
            }
        }
        else
        {
            if (eachBooking.getBookingStatus().equals("0"))
            {
                //cancel the booking
                cancelBooking();
            }
            else if (eachBooking.getBookingStatus().equals("1"))
            {
                //Make a call
                placePhoneCall();
            }
            else
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "You have booked a car with OTTO Car Rentals with booking id " + textViewTitle.getText().toString() + " $ your final price was $" + eachBooking.getFinalPrice() + "."); // Simple text and URL to share
                sendIntent.setType("text/plain");
                this.startActivity(sendIntent);
            }
        }


    }

    private void cancelBooking()
    {
        hud.show();

        //User ID is booking ID here
        db.collection("bookings").document(bookingID)
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

    private void placePhoneCall() {

        //If not ask for permission
        if (ContextCompat.checkSelfPermission(BookingDetailsActivity.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(BookingDetailsActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    1);

        }
        else
            {
            // Permission has already been granted
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:2345678909")));
        }

    }

    private void updatePayment()
    {
        hud.show();

        db.collection("bookings").document(bookingID)
                .update("paymentStatus", "Payment Complete")
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
                        Toast.makeText(BookingDetailsActivity.this, "There is a problem with the deletion.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateFeedback(String feedback)
    {
        db.collection("bookings").document(bookingID)
                .update("bookingFeedback", feedback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(BookingDetailsActivity.this, "Feedback Noted!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BookingDetailsActivity.this, "There is a problem with the submission.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getBookings()
    {
        hud.show();

        //Get bookings from firebase
        DocumentReference docRef = db.collection("bookings").document(bookingID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    hud.dismiss();

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("BookingDetails", "DocumentSnapshot data: " + document.getData());

                        eachBooking = (Booking) document.toObject(Booking.class);
                        well();

                    } else {
                        Log.d("BookingDetails", "No such document");
                    }
                } else {
                    Log.d("BookingDetails", "get failed with ", task.getException());
                }
            }
        });
    }
}