package com.matias.cleanapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity
{
    private static final String TAG = "ForgotPasswordActivity";

    private Button sendNewPasswordButton;
    private EditText enterEmailEditText;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        sendNewPasswordButton = findViewById(R.id.sendNewPasswordButton);
        enterEmailEditText = findViewById(R.id.enterEmailEditText);
        mAuth = FirebaseAuth.getInstance();

        sendNewPasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendNewPassword();
            }
        });
    }

    private void sendNewPassword()
    {
        String email = enterEmailEditText.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(email))
        {
            toastMessage(getString(R.string.PleaseenteranEmail));
        }
        else
        {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isComplete())
                    {
                        toastMessage(getString(R.string.PleasecheckyourEmailifyouwanttoresetyourpassword));
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        toastMessage(getString(R.string.ErrorOccurred) + task.getException());
                    }
                }
            });
        }

    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
