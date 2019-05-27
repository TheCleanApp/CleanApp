package com.matias.cleanapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;

public class TEST extends AppCompatActivity
{

    private static final String TAG = "TEST";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private StorageReference mStorageRef;



    ListView userListView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        userListView = findViewById(R.id.userListView);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        userListView.setAdapter(adapter);

        listUsers();

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String userId = (String) parent.getItemAtPosition(position);
                validateUser(userId);
            }
        });


    }


    private void validateUser(String userId)
    {
        StorageReference storageReference = mStorageRef.child("Images/profile_pictures/" +  userId);


        //Log.d(TAG, "validateUser: " +  storageReference.getMetadata());
        Log.d(TAG, "validateUser: " +  storageReference.getPath());
    }

    private void listUsers()
    {
        myRef.child("users").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    try
                    {
                        arrayList.add(String.valueOf(snapshot.getKey()));
                        adapter.notifyDataSetChanged();
                    }
                    catch (NullPointerException npe)
                    {
                        Log.d(TAG, "NULLPOINTER " + npe);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
