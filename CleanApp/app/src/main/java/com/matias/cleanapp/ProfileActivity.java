package com.matias.cleanapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity
{

    private static final String TAG = "ProfileActivity";
    // TextViews
    TextView profileFirstNameTextView, profileLastNameTextView, profileEmailTextView, profilePointTextView;

    // Buttons
    Button chooseProfilePictureButton, changePasswordButton, updateProfileButton;

    // ImageViews
    ImageView profilePictureImageView;

    // Firebase Realtime Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    // Firebase auth
    private FirebaseAuth mAuth;

    // Strings
    private String userId;

    // For the picture
    private final int PICK_IMAGE_REQUEST = 71;

    // Storage
    private StorageReference mStorageRef;

    // ProgressDialog
    private ProgressDialog mProgressDialog;

    // My Bitmap
    private Bitmap my_image;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Finding all the views
        profileFirstNameTextView = findViewById(R.id.profileFirstNameTextView);
        profileLastNameTextView = findViewById(R.id.profileLastNameTextView);
        profileEmailTextView = findViewById(R.id.profileEmailTextView);
        profilePointTextView = findViewById(R.id.profilePointTextView);
        chooseProfilePictureButton = findViewById(R.id.chooseProfilePictureButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        profilePictureImageView = findViewById(R.id.profilePictureImageView);

        // Firebase Realtime database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Current User + ID
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        // Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // ProgressDialog
        mProgressDialog = new ProgressDialog(ProfileActivity.this);

        getProfileInformation();
        getProfilePicture();

    }

    private void getProfilePicture()
    {
        mProgressDialog.setMessage("Loading Profile");
        mProgressDialog.show();
        StorageReference storageReference = mStorageRef.child("Images/profile_pictures/" +  userId + "/" + "Profile Picture.jpg");
        try
        {
            final File localFile = File.createTempFile("Images", "bmp");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                {
                    Log.d(TAG, "onSuccess: Getting profile picture SUCCESS");
                    my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    profilePictureImageView.setImageBitmap(my_image);
                    mProgressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Failed to get profile Picture");
                    mProgressDialog.dismiss();
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void getProfileInformation()
    {
        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                // Getting creds from database
                String firstName = (String) dataSnapshot.child("users").child(userId).child("firstName").getValue();
                String lastName = (String) dataSnapshot.child("users").child(userId).child("lastName").getValue();
                String email = (String) dataSnapshot.child("users").child(userId).child("email").getValue();

                // Updating views
                if(!firstName.equals(""))
                {
                    profileFirstNameTextView.setText(firstName);
                }
                else
                {
                    profileFirstNameTextView.setText("Update First Name");
                }
                if(!lastName.equals(""))
                {
                    profileLastNameTextView.setText(lastName);
                }
                else
                {
                    profileLastNameTextView.setText("Update Last Name");
                }
                if(!email.equals(""))
                {
                    profileEmailTextView.setText(email);
                }
                else
                {
                    profileEmailTextView.setText("Update E-mail");
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    public void profileButtonChoice(View view)
    {
        switch (view.getId())
        {
            case R.id.chooseProfilePictureButton:
            {
                Log.d(TAG,"Choose Profile Picture");
                chooseProfilePicture();
                break;
            }
            case R.id.changePasswordButton:
            {
                Log.d(TAG,"Change Password");
                changePassword();
                break;
            }
            case R.id.updateProfileButton:
            {
                Log.d(TAG,"Update Profile");
                updateProfile();
                break;
            }
        }
    }

    private void changePassword()
    {
        // TODO: 20-05-2019
    }

    private void updateProfile()
    {
        Log.d(TAG, "updateProfile");
        Intent intent = new Intent(this,UpdateProfileActivity.class);
        startActivity(intent);
        toastMessage("Updating Profile");
    }

    private void chooseProfilePicture()
    {
        Log.d(TAG, "chooseProfilePicture: Choosing picture");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        String filePath = data.getDataString();
        Uri selectedFileLocation = Uri.parse(filePath);
        uploadProfilePicture(selectedFileLocation);
    }

    private void uploadProfilePicture(Uri file)
    {
        mProgressDialog.setMessage("Uploading picture...");
        mProgressDialog.show();

        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        StorageReference storageReference = mStorageRef.child("Images/profile_pictures/" +  userId + "/" + "Profile Picture.jpg");
        storageReference.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) 
            {
                mProgressDialog.dismiss();
                toastMessage("Uploaded");
                getProfilePicture();
            }
        }).addOnFailureListener(new OnFailureListener() 
        {
            @Override
            public void onFailure(@NonNull Exception e) 
            {
                mProgressDialog.dismiss();
                toastMessage("Failed to choose picture. Please Try again or contact admin: Matias_gramkow@hotmail.com");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() 
        {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) 
            {
                Log.d(TAG, "onProgress: Progress on the Upload");
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                mProgressDialog.setMessage("Updating Profile Picture " + (int) progress+"%");
            }
        });
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
