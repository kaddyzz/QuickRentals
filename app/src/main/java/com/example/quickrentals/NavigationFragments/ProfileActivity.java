package com.example.quickrentals.NavigationFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.quickrentals.PaymentCarActivity;
import com.example.quickrentals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.kaopiz.kprogresshud.KProgressHUD;

public class ProfileActivity extends Fragment
{
    private KProgressHUD hud;
    private String updatedName = "";
    private SharedPreferences pref;
    private TextView textViewName;

    private InteractionListener mListener;

    public interface InteractionListener {
        void onFragmentInteraction();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            //init the listener
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_profile, container, false);

        textViewName = view.findViewById(R.id.textViewName);
        TextView textViewEmail = view.findViewById(R.id.textViewEmail);

        ImageView imageViewProfileImage = view.findViewById(R.id.imageViewProfileImage);

        Button buttonEditName = view.findViewById(R.id.buttonEditName);

        //Progress HUD
        hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Updating Name");

        buttonEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call alert
                alertDialogue();
            }
        });

        //Call shared  pref to get data profile
        pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode

        textViewName.setText(pref.getString("fullName", ""));
        textViewEmail.setText(pref.getString("email", ""));

        Glide.with(ProfileActivity.this).load(pref.getString("imageURL", "")).into(imageViewProfileImage);

        if(pref.getInt("loginType", 0) == 1)
        {
            buttonEditName.setVisibility(View.INVISIBLE);
        }
        else
        {
            buttonEditName.setVisibility(View.VISIBLE);
        }


        return view;
    }

    private void alertDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Enter new name.");

        // Set up the input
        final EditText input = new EditText(getContext());

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(pref.getString("fullName", ""));

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                updatedName = input.getText().toString();

                if (!updatedName.isEmpty())
                {
                    submitRequestToUpdateName();
                } else
                    {
                    Toast.makeText(getContext(), "Name can not be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void submitRequestToUpdateName()
    {
        hud.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(updatedName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            hud.dismiss();

                            //Save details in shared pref
                            SharedPreferences.Editor editor = pref.edit();

                            editor.putString("fullName", updatedName); // Storing string

                            editor.apply();

                            textViewName.setText(updatedName);

                            mListener.onFragmentInteraction();

                        }
                        else
                        {
                            hud.dismiss();
                            Toast.makeText(getContext(), "Unable to update the name.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}
