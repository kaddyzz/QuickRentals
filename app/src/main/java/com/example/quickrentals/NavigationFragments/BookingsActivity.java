package com.example.quickrentals.NavigationFragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickrentals.Adapters.BookingsAdapter;
import com.example.quickrentals.ModelClasses.Booking;
import com.example.quickrentals.ModelClasses.Cars;
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

public class BookingsActivity extends Fragment {

    private BookingsAdapter bookingsAdapter;
    private RecyclerView recyclerViewBookings;
    private FirebaseFirestore db;
    private TextView textViewTitle;
    private KProgressHUD hud;

    private List<Booking> bookingList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_bookings, container, false);

        recyclerViewBookings = view.findViewById(R.id.recyclerViewBookings);

        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();

        //Create HUD
        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");

        textViewTitle = view.findViewById(R.id.textViewTitle);

        //Call firebase to give you all the bookings
        getAllBookings();

        return view;

    }

    private void getAllBookings()
    {

        hud.show();

        //Get id
        final SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        //Get bookings from firebase
        db.collection("bookings")
                .whereEqualTo("userID",pref.getString("userID", ""))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //Dismiss HUD
                        hud.dismiss();

                        String bookingID = "";

                        //Create modal class object
                        bookingList = new ArrayList<>();

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

                                bookingsAdapter = new BookingsAdapter(bookingList, false);

                                recyclerViewBookings.setLayoutManager(new LinearLayoutManager(getActivity()));

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
    public void onResume() {
        super.onResume();
        getAllBookings();
    }

}