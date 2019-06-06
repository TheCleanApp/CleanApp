package com.matias.cleanapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matias.cleanapp.Models.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity
{

    private RecyclerView mRecyclerView;
    private AdminImageAdapter mAdapter;

    private DatabaseReference mDatabaseRef;
    private List<ImageModel> mImageModels;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mImageModels = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(getString(R.string.CleaningPicture));
        mRecyclerView.removeAllViews();


        mDatabaseRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    ImageModel imageModel = postSnapshot.getValue(ImageModel.class);
                    mImageModels.add(imageModel);
                }

                mAdapter = new AdminImageAdapter(AdminActivity.this, mImageModels);

                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                toastMessage(databaseError.getMessage());
            }
        });
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
