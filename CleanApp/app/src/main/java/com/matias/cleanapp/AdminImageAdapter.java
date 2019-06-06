package com.matias.cleanapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.matias.cleanapp.Models.ImageModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminImageAdapter extends RecyclerView.Adapter<AdminImageAdapter.ImageViewHolder>
{
    private static final String TAG = "AdminImageAdapter";
    private Context mContext;
    private List<ImageModel> mImageModels;
    private DatabaseReference mDatabaseRef;


    public AdminImageAdapter(Context context, List<ImageModel> imageModels)
    {
        mContext = context;
        mImageModels = imageModels;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder imageViewHolder, final int i)
    {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(mContext.getResources().getString(R.string.CleaningPicture));
        // Setting name on each
        final ImageModel imageModelCurrent = mImageModels.get(i);
        imageViewHolder.textViewName.setText(imageModelCurrent.getName());

        // Click on each
        imageViewHolder.removeImageButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mImageModels.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, getItemCount());
                mDatabaseRef.child(imageModelCurrent.getId()).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.getValue() != null)
                        {
                            mImageModels.clear();
                            dataSnapshot.getRef().removeValue();
                        }
                        else
                        {
                            Log.d(TAG, mContext.getResources().getString(R.string.nof));
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
                notifyDataSetChanged();
            }
        });


        // Loading picture to each
        Picasso.with(mContext)
                .load(imageModelCurrent.getImageUrl())
                .placeholder(R.drawable.cleanpicture)
                .fit()
                .centerInside()
                .into(imageViewHolder.imageView);
    }
    

    @Override
    public int getItemCount()
    {
        return mImageModels.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewName;
        public ImageView imageView;
        public Button removeImageButton;

        public ImageViewHolder(View itemView)
        {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
            removeImageButton = itemView.findViewById(R.id.removeImageButton);
        }
    }

}
