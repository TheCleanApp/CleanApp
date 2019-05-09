package com.matias.cleanapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class StartCleanUpActivity extends AppCompatActivity
{

    private static final String TAG = "StartCleanUpActivity";

    //Declare variables
        // Buttons
    private Button choosePicture, uploadPicture;

        // ImageViews
    private ImageView pictureImageView;

        // ProgressDialog
    private ProgressDialog mProgressDialog;

        // Storage
    private StorageReference mStorageRef;

        // Firebase
    private FirebaseAuth auth;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startcleanup);

        // ImageViews
        pictureImageView = findViewById(R.id.pictureImageView);

        // Buttons
        choosePicture = findViewById(R.id.choosePictureButton);
        uploadPicture = findViewById(R.id.uploadPictureButton);

        // ProgressDialog
        mProgressDialog = new ProgressDialog(StartCleanUpActivity.this);

        // Firebase
        auth = FirebaseAuth.getInstance();

        // Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //checkFilePermissions();

        choosePicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                chooseImage();
            }
        });

        uploadPicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mProgressDialog.setMessage("Uploading picture...");
                mProgressDialog.show();

                // get the current user
                FirebaseUser user = auth.getCurrentUser();
                String userId = user.getUid();
                String currentDateString = DateFormat.getDateInstance().format(new Date());
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                StorageReference storageReference = mStorageRef.child("Images/users/" +  userId + "/" + currentDateString + "/" + "Start Picture/" + currentDateTimeString + ".jpg");

                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        mProgressDialog.dismiss();
                        toastMessage("Uploaded");
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        mProgressDialog.dismiss();
                        toastMessage("Something went wrong with the Upload.");
                        toastMessage("Please try again or contact 'Matias_gramkow@hotmail.com'");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        mProgressDialog.setMessage("Uploaded " + (int) progress+"%");
                    }
                });
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /*
    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = StartCleanUpActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += StartCleanUpActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
    */

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        pictureImageView.setImageBitmap(bitmap);
    }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                pictureImageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
