package com.example.user.customgalleryfinal.Models;

import java.util.List;

/**
 * Created by ${Chandran} on 1/6/15.
 */
public class GalleryDetailsCell {
    private String mImagePath;
    private String mThumbnailPath;

    public GalleryDetailsCell(String imagePath, String thumbPath) {
        mImagePath = imagePath;
        mThumbnailPath = thumbPath;
    }
    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        mThumbnailPath = thumbnailPath;
    }
}
