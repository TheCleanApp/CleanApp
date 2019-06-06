package com.matias.cleanapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartCleanUpActivity extends AppCompatActivity
{

    private static final String TAG = "StartCleanUpActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    //Declare variables
        // Buttons
    private Button chooseImageFromPhoneButton, uploadStartImageButton;

        // ImageViews
    private ImageView pictureImageView;

        // ProgressDialog
    private ProgressDialog mProgressDialog;

        // Storage
    private StorageReference mStorageRef;
    private DatabaseReference mDatabseRef;

        // Firebase
    private FirebaseAuth auth;

    private StorageTask uploadingRunning;

    // Image Handling
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
        chooseImageFromPhoneButton = findViewById(R.id.chooseImageFromPhoneButton);
        uploadStartImageButton = findViewById(R.id.uploadStartImageButton);
        // ProgressDialog
        mProgressDialog = new ProgressDialog(StartCleanUpActivity.this);

        // Firebase
        auth = FirebaseAuth.getInstance();

        // Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabseRef = FirebaseDatabase.getInstance().getReference();

        // Getting starting Image
        int imageResource = getResources().getIdentifier("@drawable/cleanpicture", null, this.getPackageName());
        pictureImageView.setImageResource(imageResource);

    }

    public void buttonChoice(View view)
    {
        switch (view.getId())
        {
            case R.id.chooseImageFromPhoneButton:
            {
                chooseImageFromPhone();
                break;
            }
            case R.id.uploadStartImageButton:
            {
                // Does so the user only can click on upload once.
                if (uploadingRunning != null && uploadingRunning.isInProgress())
                {
                    toastMessage(getString(R.string.ImageModelInProgresspleasewait));
                    break;
                }
                else
                {
                    uploadStartImage();
                    break;
                }
            }
        }
    }

    private void uploadStartImage()
    {
        uploadStartImageButton.setEnabled(false);
        mProgressDialog.setMessage(getString(R.string.Uploadingpicture));
        mProgressDialog.show();

        // get the current user
        final FirebaseUser user = auth.getCurrentUser();
        final String uploadId = mDatabseRef.push().getKey();
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


        final StorageReference storageReference = mStorageRef.child("Images/wallImages/" + uploadId + "/" +  currentDateTimeString + ".jpg");

        uploadingRunning = storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        Map<String,Object> taskMap = new HashMap<>();
                        String url = uri.toString();

                        taskMap.put(getString(R.string.name), user.getEmail());
                        taskMap.put(getString(R.string.imageUrl), url);
                        taskMap.put(getString(R.string.id), uploadId);
                        mDatabseRef.child(getString(R.string.CleaningPicture)).child(uploadId).updateChildren(taskMap);
                    }
                });
                mProgressDialog.dismiss();
                toastMessage(getString(R.string.Uploaded));
                Intent intent = new Intent(StartCleanUpActivity.this, MenuActivity.class);
                startActivity(intent);
                uploadStartImageButton.setEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                mProgressDialog.dismiss();
                toastMessage(getString(R.string.SomethingwentwrongwiththeImageModel));
                uploadStartImageButton.setEnabled(true);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
            {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                mProgressDialog.setMessage(getString(R.string.UploadedWithSpace) + (int) progress+"%");
            }
        });
    }

    private void chooseImageFromPhone()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.SelectPicture)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                pictureImageView.setImageBitmap(bitmap);
                uploadStartImageButton.setVisibility(View.VISIBLE);
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
