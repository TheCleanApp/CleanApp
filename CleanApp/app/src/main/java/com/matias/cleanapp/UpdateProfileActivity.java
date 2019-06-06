package com.matias.cleanapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity
{
    private static final String TAG = "UpdateProfileActivity";
    // Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    // Buttons
    Button updateCredentialsButton;

    // EditTexts
    EditText updateFirstNameEditText,updateLastNameEditText;

    // Strings
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);

        // Finding all the views
        updateCredentialsButton = findViewById(R.id.updateCredentialsButton);
        updateFirstNameEditText = findViewById(R.id.updateFirstNameEditText);
        updateLastNameEditText = findViewById(R.id.updateLastNameEditText);

        // Firebase
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        updateCredentialsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Intent intent = new Intent(UpdateProfileActivity.this,ProfileActivity.class);
                        startActivity(intent);

                        // Updating database
                        String firstName = updateFirstNameEditText.getText().toString();
                        String lastName = updateLastNameEditText.getText().toString();
                        myRef.child("users").child(userId).child("firstName").setValue(firstName);
                        myRef.child("users").child(userId).child("lastName").setValue(lastName);
                        toastMessage(getString(R.string.ProfileUpdated));
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        });
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
