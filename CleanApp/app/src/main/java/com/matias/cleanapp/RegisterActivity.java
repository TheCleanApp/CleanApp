package com.matias.cleanapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.matias.cleanapp.Models.UserModel;

public class RegisterActivity extends AppCompatActivity
{

    private static final String TAG = "RegisterActivity";

    // Edittexts
    private EditText registerFirstNameEditText,registerLastNameEditText,registerEmailEditText,registerPasswordEditText;

    // Textviews
    private TextView registerTextView;

    // Buttons
    private Button registerButton;

    // Firebase

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseUsers;

    // Progressdialog
    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG,"RegisterActivity");

        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        // Firebase
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Edittexts
        registerEmailEditText = findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);

        // TextViews
        registerTextView = findViewById(R.id.registerTextView);

        //Buttons
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PD.show();
                register();

            }
        });

    }

    private void register()
    {
        final String email = registerEmailEditText.getText().toString().trim();
        final String password = registerPasswordEditText.getText().toString().trim();

        // Checking if all the fields are filled
        boolean validatedFields = validate(new EditText[] {registerEmailEditText,registerPasswordEditText});

        if (validatedFields)
        {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (!task.isSuccessful())
                    {
                        toastMessage("Failed to register, please contact administator: 'Matias_gramkow@hotmail.com'");
                    }
                    else
                    {
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);

                        startActivity(intent);
                        finish();
                    }
                    PD.dismiss();
                }
            });

            /*
            // Tjekker om der er en email som matcher i databasen
            Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(email);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.getChildrenCount()>0)
                    {
                        Toast.makeText(RegisterActivity.this,"Email already taken", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String id = databaseUsers.push().getKey();

                        UserModel userModel = new UserModel(id, firstName, lastName, email, password);

                        databaseUsers.child(id).setValue(userModel);

                        Toast.makeText(RegisterActivity.this,"User added", Toast.LENGTH_SHORT).show();

                        /*
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        Bundle extras = new Bundle();

                        extras.putString("email",email);
                        extras.putString("password",password);
                        intent.putExtras(extras);
                        startActivity(intent);

                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });
            */
        }
        else
        {
            Toast.makeText(this,"All fields must be filled", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(EditText[] fields)
    {
        for(int i = 0; i < fields.length; i++)
        {
            EditText currentField = fields[i];
            if(currentField.getText().toString().length() <= 0)
            {
                return false;
            }
        }
        return true;
    }
    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
