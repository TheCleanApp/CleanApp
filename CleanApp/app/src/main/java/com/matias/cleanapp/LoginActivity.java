package com.matias.cleanapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                    Log.d(TAG, "onAuthStateChange:signed in:" + user.getUid());
                    toastMessage("Welcome " + user.getEmail() + ". Have a clean day");
                    skipLogin();
                }
                else
                {
                    // User is signed out
                    Log.d(TAG,"onAuthStateChange:signed out");
                    toastMessage("Not Logged in.");
                }
            }
        };
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                if(!email.equals("") && !password.equals(""))
                {
                    mAuth.signInWithEmailAndPassword(email,password).addOnFailureListener(LoginActivity.this, new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            toastMessage("Wrong email or password. Please register or try again");
                        }
                    });
                }
                else
                {
                    toastMessage("Please fill both the Email and Password field.");
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

    private void skipLogin()
    {
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
    }


    private void register()
    {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void login()
    {

    }

    private void toastMessage(String message)
{
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
}

}
