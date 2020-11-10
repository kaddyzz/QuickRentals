package com.example.quickrentals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.quickrentals.ModelClasses.Cars;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class CarsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CarsAdapter carsAdapter;
    KProgressHUD kProgressHUD;
    private String startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        //Instantiations
        TextView textViewStartDate = findViewById(R.id.textViewStartDate);
        TextView textViewEndDate = findViewById(R.id.textViewEndDate);


        //Create modal class object
        final List<Cars> carsList = new ArrayList<>();


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        kProgressHUD.show();

        //Get cars
        db.collection("cars")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        //Dismiss HUD
                        kProgressHUD.dismiss();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FIREBASE", document.getId() + " => " + document.get("carMake"));

                                //Add each car to car list
                                Cars cars = document.toObject(Cars.class);
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