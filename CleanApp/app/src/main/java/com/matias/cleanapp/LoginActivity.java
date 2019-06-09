package com.matias.cleanapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.databinding.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.matias.cleanapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";

    private ActivityLoginBinding mBinding;

    private EditText loginEmail, loginPassword;
    private TextView loginTextView, registerTextView, forgotPasswordTextView;

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

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // EditTexts
        loginEmail = findViewById(R.id.loginEmailEditText);
        loginPassword = findViewById(R.id.loginPasswordEditText);

        // TextViews
        loginTextView = findViewById(R.id.loginTextView);
        registerTextView = findViewById(R.id.registerTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);


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
                if (user != null & loginEmail.getText().toString().equals(""))
                {
                    animateButtonWidth();
                    fadeOutTextAndSetProgressDialog();
                    nextAction();
                }
            }
        };

        registerTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                register();
            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                forgotPassword();
            }
        });


    }

    public void login(View view)
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

            }).addOnSuccessListener(new OnSuccessListener<AuthResult>()
            {
                @Override
                public void onSuccess(AuthResult authResult)
                {
                    animateButtonWidth();
                    fadeOutTextAndSetProgressDialog();
                    nextAction();

                }
            });

        }
        else
        {
            toastMessage(getString(R.string.PleasefillboththeEmailandPasswordfield));
        }

    }

    private void animateButtonWidth()
    {
        ValueAnimator anim = ValueAnimator.ofInt(mBinding.loginBtn.getMeasuredWidth(), getFinalWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mBinding.loginBtn.getLayoutParams();
                layoutParams.width = value;
                mBinding.loginBtn.requestLayout();
            }
        });

        anim.setDuration(350);
        anim.start();
    }

    private void fadeOutTextAndSetProgressDialog()
    {
        mBinding.loginText.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showProgressDialog();
            }
        }).start();
    }

    private void showProgressDialog()
    {
        mBinding.progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    private void nextAction()
    {
        new Handler().postDelayed(new Runnable()
        {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run()
            {
                revealButton();
                fadeOutProgressDialog();
                delayedStartNextAction();

            }
        }, 2000);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealButton()
    {
        mBinding.loginBtn.setElevation(0f);
        mBinding.revealView.setVisibility(View.VISIBLE);

        int x = mBinding.revealView.getWidth();
        int y = mBinding.revealView.getHeight();

        int startX = (int) (getFinalWidth() / 2 + mBinding.loginBtn.getX());
        int startY = (int) (getFinalWidth() / 2 + mBinding.loginBtn.getY());

        float radius = Math.max(x,y) * 1.2f;
        Animator reveal = ViewAnimationUtils.createCircularReveal(mBinding.revealView, startX, startY, getFinalWidth(), radius);
        reveal.setDuration(350);
        reveal.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                finish();
            }
        });
        reveal.start();
    }

    private void fadeOutProgressDialog()
    {
        mBinding.progressBar.animate().alpha(0f).setDuration(200).start();
    }

    private void delayedStartNextAction()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                login();
            }
        }, 100);
    }



    private int getFinalWidth()
    {
        return (int) getResources().getDimension(R.dimen.get_width);
    }

    private void forgotPassword()
    {
        Intent intent = new Intent(this,ForgotPasswordActivity.class);
        startActivity(intent);
    }


    private void login()
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
