package com.example.user.customgalleryfinal.Models;

/**
 * Created by ${Chandran} on 25/5/15.
 */
public class GalleryBucketCell {
    private int mBucketId;
    private int mTotalCount;
    private String mBucketName;
    private String mDateTaken;
    private String mThumbnailPath;

    public int getBucketId() {
        return mBucketId;
    }

    public void setBucketId(int bucketId) {
        mBucketId = bucketId;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    public String getBucketName() {
        return mBucketName;
    }

    public void setBucketName(String bucketName) {
        mBucketName = bucketName;
    }

    public String getDateTaken() {
        return mDateTaken;
    }

    public void setDateTaken(String dateTaken) {
        mDateTaken = dateTaken;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        mThumbnailPath = thumbnailPath;
    }
}
