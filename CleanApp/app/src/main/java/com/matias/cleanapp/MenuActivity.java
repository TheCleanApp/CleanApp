package com.matias.cleanapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
    private static final String STATE_COUNTER = "counter";

    Button signoutButton, startCleanUpButton, profileButton, aboutButton, adminButton, counterButton;

    TextView welcomeTextView, counterTextView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private boolean welcome = false;

    private String userId;

    private int mCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        userId = user.getUid();

        welcomeTextView = findViewById(R.id.welcomeTextView);
        counterTextView = findViewById(R.id.counterTextView);

        signoutButton = findViewById(R.id.logoutButton);
        startCleanUpButton = findViewById(R.id.startCleanUpButton);
        profileButton = findViewById(R.id.profileButton);
        aboutButton = findViewById(R.id.aboutButton);
        adminButton = findViewById(R.id.adminButton);
        counterButton = findViewById(R.id.counterButton);


        // Shows that i handle the state
        if (savedInstanceState != null)
        {
            mCounter = savedInstanceState.getInt(STATE_COUNTER, 0);
            toastMessage(getString(R.string.Restoringdata));
        }





        counterTextView.setText(Integer.toString(mCounter));

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
                try
                {
                    boolean isAdmin = (boolean) dataSnapshot.child("users").child(userId).child("isAdmin").getValue();

                    if (isAdmin)
                    {
                        adminButton.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        adminButton.setVisibility(View.INVISIBLE);
                    }
                }
                catch (NullPointerException nullpointer)
                {
                    logOut();
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

            case R.id.adminButton:
            {
                admin();
                break;
            }
            case R.id.counterButton:
            {
                counter();
                break;
            }

        }
    }

    private void counter()
    {
        mCounter++;
        counterTextView.setText(Integer.toString(mCounter));
    }

    private void admin()
    {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }


    private void profile()
    {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
        toastMessage(getString(R.string.YourProfile));

    }

    private void about()
    {
        Log.d(TAG, "About Button");

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setMessage(getString(R.string.CLEANUPABOUTTEXT)).setNegativeButton(getString(R.string.Nice), dialogClickListener).show();
    }

    private void startCleanUp()
    {
        Intent intent = new Intent(this,StartCleanUpActivity.class);
        startActivity(intent);
        toastMessage(getString(R.string.StartCleanUp));
    }

    private void logOut()
    {
        mAuth.signOut();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        toastMessage(getString(R.string.Signingout));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_COUNTER, mCounter);
    }

    private void toastMessage(String message)
    {
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
