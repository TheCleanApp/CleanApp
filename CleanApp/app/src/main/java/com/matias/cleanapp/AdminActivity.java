package com.matias.cleanapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity
{

    private static final String TAG = "AdminActivity";

    // Firebase Realtime Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    DatabaseReference usersReference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        // Firebase Realtime database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        listUsers();

    }

    private void listUsers()
    {
        // TODO: 21-05-2019 ADMIN SE USERS OG VALIDER DEM

    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
