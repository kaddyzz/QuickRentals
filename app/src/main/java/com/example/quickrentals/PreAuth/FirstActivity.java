package com.example.quickrentals.PreAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quickrentals.NavigationFragments.NavigationMainActivity;
import com.example.quickrentals.R;
import com.example.quickrentals.Vendor.VendorBookingsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaopiz.kprogresshud.KProgressHUD;

public class FirstActivity extends AppCompatActivity {

    //Google
    LottieAnimationView loaderAnimation;

    //Google sign in
    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    //Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    EditText emailField;
    EditText passwordField;
    KProgressHUD hud;

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

        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);

        //Progress HUD
        hud = KProgressHUD.create(FirstActivity.this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");

        //Firebase instance
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Start Google
        callGoogleSignIn();

        //Button Actions

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Do something on click of login
                loginAction();

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

        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Google Login
                signIn();
            }
        });
    }

    @Override
    public void onBackPressed() {

        //Do nothing on back press
        Toast.makeText(this, "No further back allowed.", Toast.LENGTH_SHORT).show();
    }

    //Button methods
    void loginAction(){

        //Check for empty fields and then login

        if (emailField.getText().toString().equals(""))
        {
            Toast.makeText(this, "Please enter email address.", Toast.LENGTH_SHORT).show();
        }
        else if (!isValidEmail(emailField.getText().toString()))
        {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
        }
        else if(passwordField.getText().toString().equals(""))
        {
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (emailField.getText().toString().equals("vendor@otto.com") && passwordField.getText().toString().equals("vendor"))
            {
                startActivity(new Intent(FirstActivity.this, VendorBookingsActivity.class));
            }
            else
            {
                //Get authentication using firebase
                checkInFirebase();
            }

        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void checkInFirebase() {

        //Start HUD
        hud.show();

        //Firebase authentication
        mAuth.signInWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE ::", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Dismiss HUD
                            hud.dismiss();

                            try {
                                String fullName = user.getDisplayName();
                                String email = user.getEmail();
                                Uri imageURL = user.getPhotoUrl();

                                //Save details in shared pref
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString("fullName", fullName); // Storing string
                                editor.putString("email", email); // Storing string
                                editor.putString("imageURL", imageURL.toString()); // Storing string
                                editor.putInt("loginType",0);
                                editor.putString("userID",user.getUid());

                                editor.apply(); // commit changes

                                startActivity(new Intent(FirstActivity.this, NavigationMainActivity.class));
                            }
                            catch (Exception ex)
                            {
                                Toast.makeText(FirstActivity.this, "Error occured!!", Toast.LENGTH_SHORT).show();
                            }



                        } else {
                            // If sign in fails, display a message to the user.
                            hud.dismiss();

                            Log.w("FIREBASE ::", "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirstActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Google Methods
    void callGoogleSignIn()
    {

        //Client ID
        //374970919737-dpoot02819lb74b9f4o46vsiudpou0o9.apps.googleusercontent.com

        //Client Secret
        //U_Izq0rCZNkkIhrR7oe1bQ1n

        // Configure sign-in to request the user's ID, email address, and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            try {
                String fullName = account.getDisplayName();
                String email = account.getEmail();
                Uri imageURL = account.getPhotoUrl();

                //Save details in shared pref
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("fullName", fullName); // Storing string
                editor.putString("email", email); // Storing string
                editor.putString("imageURL", imageURL.toString()); // Storing string
                editor.putInt("loginType",1);
                editor.putString("userID",account.getId());

                editor.apply(); // commit changes

                startActivity(new Intent(this, NavigationMainActivity.class));
            }
            catch (Exception ex)
            {
                Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
            }


        } catch (ApiException e) {
            Log.w("GoogleSignInError", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {

        //Check for existing account
        //Save details in shared pref
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        if (pref.contains("userID"))
        {
            startActivity(new Intent(FirstActivity.this, NavigationMainActivity.class));
        }

        super.onStart();
    }

}