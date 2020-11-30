package com.example.quickrentals.PreAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quickrentals.NavigationFragments.NavigationMainActivity;
import com.example.quickrentals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class SignUp extends AppCompatActivity {

    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPassword;
    ImageView imageProfile;

    Uri imageURI;

    String imageSelected = "";

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseFirestore db;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    //Loading animation
    KProgressHUD kProgressHUD;

    //Email Regex
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Instansiate values
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);


        Button signUpButton = findViewById(R.id.buttonSignUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpAction();
            }
        });

        imageProfile = findViewById(R.id.imageViewUserImage);

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //Firebase instance
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        //Progress HUD
        kProgressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Please wait");
    }

    private void selectImage() {

        //Setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Set title
        builder.setTitle("Notice");

        //Set message
        builder.setMessage("Choose an action");

        //Add the buttons
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                openGallery();
            }
        });
        builder.setNegativeButton("Cancel", null);

        //Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openGallery() {


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        } else {
            // Permission has already been granted

            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:

                    try {
                        //data.getData returns the content URI for the selected Image
                        imageURI = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageURI);
                        final Bitmap selectedImageInBM = BitmapFactory.decodeStream(imageStream);
                        //image_view.setImageBitmap(selectedImage);
                        imageProfile.setImageBitmap(selectedImageInBM);
                        imageSelected = "Image Selected";

                    } catch (FileNotFoundException e) {
                    }

                    break;
            }

    }

    //Button methods
    void signUpAction() {

        //Check for empty fields and then signup

        if (imageSelected.isEmpty()) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();

        }else if (editTextName.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter name.", Toast.LENGTH_SHORT).show();

        } else if (editTextEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter email address.", Toast.LENGTH_SHORT).show();

        } else if (!isValidEmail(editTextEmail.getText().toString())) {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();

        } else if (editTextPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter password.", Toast.LENGTH_SHORT).show();

        } else if (editTextPassword.getText().toString().trim().length() < 7) {
            Toast.makeText(this, "Password should be at least 7 digits.", Toast.LENGTH_SHORT).show();

        } else {

            //Firebase save data and upload image
            uploadImage();
        }
    }


    private void uploadImage() {

        if(imageURI != null)
        {
            kProgressHUD.show();

            //Upload image to Storage and get the url
            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("FIREBASE: ", "Uploaded image URL: "+ uri.toString());
                            imageSelected = uri.toString();

                            //Create new user and use image URL
                            addAuthUser();
                        }
                    });
                }
            });
        }
    }

    private void addAuthUser()
    {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE :", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //createUser();
                            updateUserProfile();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FIREBASE :", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            //Failed, still dismiss the HUD
                            kProgressHUD.dismiss();
                        }
                    }
                });
    }

    private void updateUserProfile()
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editTextName.getText().toString().trim())
                .setPhotoUri(Uri.parse(imageSelected))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "User profile updated.");

                            //Dismiss HUD
                            kProgressHUD.dismiss();

                            //Log.d("Firebase: ", "User added with ID: " + documentReference.getId());

                            //Save details in shared pref
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();

                            editor.putString("fullName", editTextName.getText().toString()); // Storing string
                            editor.putString("email", editTextEmail.getText().toString()); // Storing string
                            editor.putString("imageURL",imageSelected); // Storing string
                            editor.putInt("loginType", 0);
                            editor.putString("userID",user.getUid());

                            editor.apply(); // commit changes

                            startActivity(new Intent(SignUp.this, NavigationMainActivity.class));

                        }
                    }
                });
    }
}