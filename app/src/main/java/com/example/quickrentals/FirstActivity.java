package com.example.quickrentals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //Object Instantiation
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonSignup = findViewById(R.id.buttonSignUp);
        Button buttonFBLogin = findViewById(R.id.buttonFBLogin);
        Button buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        Button buttonForgotPassword = findViewById(R.id.buttonForgotPassword);


        //Button Actions

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Do something on click of login
                startActivity(new Intent(FirstActivity.this, WelcomeActivity.class));

            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Go to forgot password screen
                startActivity(new Intent(FirstActivity.this, ForgotPasswordActivity.class));
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Go to signup
                startActivity(new Intent(FirstActivity.this, SignUp.class));
            }
        });


    }
}