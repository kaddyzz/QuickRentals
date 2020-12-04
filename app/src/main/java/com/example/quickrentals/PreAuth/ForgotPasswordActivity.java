package com.example.quickrentals.PreAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickrentals.R;
import com.example.quickrentals.Vendor.VendorBookingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Instantiation
        editTextEmail = findViewById(R.id.editTextEmail);
        Button buttonForgotPassword = findViewById(R.id.buttonForgotPassword);

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Reset password email
                textFieldValidation();
            }
        });
    }

    //Button methods
    void textFieldValidation(){

        //Check for empty fields and then login

        if (editTextEmail.getText().toString().equals(""))
        {
            Toast.makeText(this, "Please enter email address.", Toast.LENGTH_SHORT).show();
        }
        else if (!isValidEmail(editTextEmail.getText().toString()))
        {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
        }
        else
        {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(editTextEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "An email with the password reset link is sent.", Toast.LENGTH_SHORT).show();
                                Log.d("FORGOT PASSWORD:", "Email sent.");
                                finish();
                            }
                        }
                    });
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}