package com.matias.cleanapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class EndCleanUpActivity extends AppCompatActivity
{

    // Timer
    private Chronometer timer;
    private boolean running;
    private long pauseOffset;

    // Buttons
    Button chooseAfterPictureButton, uploadAfterPictureButton;

    // Textviews
    TextView endCleanUpInfoTextView, cleaningTimerTextView;

    // Imageviews
    ImageView afterPictureImageView;

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
        setContentView(R.layout.activity_endcleanup);

        // Timer
        timer = findViewById(R.id.timer);

        // Buttons
        chooseAfterPictureButton = findViewById(R.id.chooseAfterCleaningPictureButton);
        uploadAfterPictureButton = findViewById(R.id.uploadAfterPictureButton);

        // TextViews
        endCleanUpInfoTextView = findViewById(R.id.endCleanUpInfoTextView);
        cleaningTimerTextView = findViewById(R.id.cleaningTimerTextView);

        //ImageViews
        afterPictureImageView = findViewById(R.id.afterPictureImageView);

        // ProgressDialog
        mProgressDialog = new ProgressDialog(EndCleanUpActivity.this);

        // Firebase
        auth = FirebaseAuth.getInstance();

        // Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

        startTimer();

        chooseAfterPictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chooseImage();

            }
        });

        uploadAfterPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mProgressDialog.setMessage("Uploading picture...");
                mProgressDialog.show();

                // get the current user
                FirebaseUser user = auth.getCurrentUser();
                String userId = user.getUid();
                String currentDateString = DateFormat.getDateInstance().format(new Date());
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                StorageReference storageReference = mStorageRef.child("Images/users/" +  userId + "/" + currentDateString + "/" + "After Picture/" + currentDateTimeString + ".jpg");


                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        mProgressDialog.dismiss();
                        toastMessage("Uploaded");
                        Intent intent = new Intent(EndCleanUpActivity.this, MenuActivity.class);
                        startActivity(intent);
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

    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        timer.setVisibility(View.INVISIBLE);
        cleaningTimerTextView.setVisibility(View.INVISIBLE);
        endCleanUpInfoTextView.setVisibility(View.INVISIBLE);
        uploadAfterPictureButton.setVisibility(View.VISIBLE);
        afterPictureImageView.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                afterPictureImageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void startTimer()
    {
        if (!running)
        {
            timer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            timer.start();
            running = true;
        }
    }

    public void pauseTimer()
    {
        if (running)
        {
            timer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - timer.getBase();
            running = false;
        }
    }

    public void resetTimer()
    {
        timer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    private void toastMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
