package com.matias.cleanapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

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
    private DatabaseReference myRef;

    // Progressdialog
    private ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        PD = new ProgressDialog(this);
        PD.setMessage(getString(R.string.Loading));
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        // Firebase
        myRef = FirebaseDatabase.getInstance().getReference();
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
        final String email = registerEmailEditText.getText().toString().trim().toLowerCase();
        final String password = registerPasswordEditText.getText().toString().trim().toLowerCase();

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
                        toastMessage(getString(R.string.contactAdminRegisterFailure));
                    }
                    else
                    {
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);

                        startActivity(intent);

                        FirebaseUser user = mAuth.getCurrentUser();
                        String userID = user.getUid();

                        Map<String,Object> taskMap = new HashMap<>();
                        taskMap.put("email", email);
                        taskMap.put("isAdmin", false);
                        taskMap.put("firstName", "");
                        taskMap.put("lastName", "");
                        myRef.child("users/").child(userID).updateChildren(taskMap);
                        finish();
                    }
                    PD.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    toastMessage(getString(R.string.contactAdminRegisterFailure));
                }
            });
        }
        else
        {
            toastMessage(getString(R.string.Allfieldsmustbefilled));
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
