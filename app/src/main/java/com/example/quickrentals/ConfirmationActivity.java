package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        Button buttonGoToHome = findViewById(R.id.buttonGoToHome);

        buttonGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go Back to home

                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", true);
                intent.putExtra("rule", "FREQ=YEARLY");
                intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                intent.putExtra("title", "A Test Event from android app");

                startActivity(intent);


                //startActivity(new Intent(ConfirmationActivity.this, NavigationMainActivity.class));
            }
        });
    }
}