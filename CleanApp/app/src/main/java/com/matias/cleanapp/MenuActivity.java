package com.matias.cleanapp;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MenuActivity extends AppCompatActivity
{

    private static final String TAG = "MenuActivity";

    // Buttons
    Button signoutButton, startCleanUpButton, profileButton, newsButton, aboutButton;

    // Textviews
    TextView welcomeTextView;

    // Firebase Realtime Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    // Firebase auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // Firebase Realtime database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();

        // TextViews
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Buttons
        signoutButton = findViewById(R.id.logoutButton);
        startCleanUpButton = findViewById(R.id.startCleanUpButton);
        profileButton = findViewById(R.id.profileButton);
        newsButton = findViewById(R.id.newsButton);
        aboutButton = findViewById(R.id.aboutButton);

    }

    public void buttonChoice(View view)
    {
        switch (view.getId())
        {
            case R.id.startCleanUpButton:
            {
                startCleanUp();
                break;
            }
            case R.id.logoutButton:
            {
                logOut();
                break;
            }
            case R.id.aboutButton:
            {
                about();
                break;
            }
            case R.id.profileButton:
            {
                profile();

                break;
            }
            case R.id.newsButton:
            {
                news();
                break;
            }
        }
    }

    private void news()
    {

    }

    private void profile()
    {

    }

    private void about()
    {

    }

    private void startCleanUp()
    {
        Intent intent = new Intent(this,StartCleanUpActivity.class);
        startActivity(intent);
        toastMessage("Start CleanUp");
    }

    private void logOut()
    {
        mAuth.signOut();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        toastMessage("Signing out");
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
