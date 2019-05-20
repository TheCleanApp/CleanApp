package com.matias.cleanapp;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity
{

    private static final String TAG = "MenuActivity";

    // Buttons
    Button signoutButton, startCleanUpButton, profileButton, newsButton, aboutButton, adminButton;

    // Textviews
    TextView welcomeTextView;

    // Firebase Realtime Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    // Firebase auth
    private FirebaseAuth mAuth;

    // Strings
    private String userId;

    // Booleans
    private Boolean isAdmin;

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
         // Current User + ID
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();


        // TextViews
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Buttons
        signoutButton = findViewById(R.id.logoutButton);
        startCleanUpButton = findViewById(R.id.startCleanUpButton);
        profileButton = findViewById(R.id.profileButton);
        newsButton = findViewById(R.id.newsButton);
        aboutButton = findViewById(R.id.aboutButton);
        adminButton = findViewById(R.id.adminButton);

        // Checking if the user is Admin or not.
        checkIfUserIsAdmin();

    }

    private void checkIfUserIsAdmin()
    {
        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                boolean isAdmin = (boolean) dataSnapshot.child("users").child(userId).child("isAdmin").getValue();
                Log.d(TAG, "IsAdmin?: " + isAdmin);
                if (isAdmin)
                {
                    adminButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    adminButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
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
            case R.id.adminButton:
            {
                admin();
                break;
            }
        }
    }

    private void admin()
    {
        Log.d(TAG, "Admin Button");
    }

    private void news()
    {
        Log.d(TAG, "News Button");
    }

    private void profile()
    {
        Log.d(TAG, "Profile Button");
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
        toastMessage("Your Profile");

    }

    private void about()
    {
        Log.d(TAG, "About Button");
    }

    private void startCleanUp()
    {
        Log.d(TAG, "startCleanUp Button");
        Intent intent = new Intent(this,StartCleanUpActivity.class);
        startActivity(intent);
        toastMessage("Start CleanUp");
    }

    private void logOut()
    {
        Log.d(TAG, "Log Out Button");
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
