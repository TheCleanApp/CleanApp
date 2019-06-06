package com.matias.cleanapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.matias.cleanapp.Models.UserModel;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";


    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView loginTextView, registerTextView;

    // Firebase Realtime Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    // Firebase auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // EditTexts
        loginEmail = findViewById(R.id.loginEmailEditText);
        loginPassword = findViewById(R.id.loginPasswordEditText);

        // TextViews
        loginTextView = findViewById(R.id.loginTextView);
        registerTextView = findViewById(R.id.registerTextView);

        // Buttons
        loginButton = findViewById(R.id.loginButton);

        // Firebase Realtime database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
                    //toastMessage("Welcome " + user.getEmail() + ". Have a clean day");
                    toastMessage(getString(R.string.Welcome) + user.getEmail() + getString(R.string.Haveacleanday));
                    Login();
                }
                else
                {
                    // User is signed out
                    toastMessage(getString(R.string.NotLoggedin));
                }
            }
        };
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = loginEmail.getText().toString().toLowerCase();
                String password = loginPassword.getText().toString().toLowerCase();
                if(!email.equals("") && !password.equals(""))
                {
                    mAuth.signInWithEmailAndPassword(email,password).addOnFailureListener(LoginActivity.this, new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            toastMessage(getString(R.string.WrongemailorpasswordPleaseregisterortryagain));
                        }
                    });
                }
                else
                {
                    toastMessage(getString(R.string.PleasefillboththeEmailandPasswordfield));
                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                register();
            }
        });


    }

    private void Login()
    {

        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
    }


    private void register()
    {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    private void toastMessage(String message)
{
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
}

}
