package com.matias.cleanapp.Models;

public class ImageModel
{
    private String mName;
    private String mImageUrl;
    private String mId;

    public ImageModel()
    {

    }

    public ImageModel(String name, String imageUrl, String id)
    {
        if (name.trim().equals(""))
        {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
        mId = id;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        mImageUrl = imageUrl;
    }

    public String getId()
    {
        return mId;
    }

    public void setId(String mId)
    {
        this.mId = mId;
    }
}
