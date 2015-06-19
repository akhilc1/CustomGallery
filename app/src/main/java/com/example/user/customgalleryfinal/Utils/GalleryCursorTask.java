package com.example.user.customgalleryfinal.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.user.customgalleryfinal.Activities.GalleryDetailsActivity;
import com.example.user.customgalleryfinal.Models.GalleryDetailsCell;

import java.util.ArrayList;

/**
 * Created by ${Chandran} on 1/6/15.
 */
public class GalleryCursorTask extends AsyncTask<Void, GalleryDetailsCell, Void> {
    private Context mContext;
    private Cursor mCursor;
    private String mBucketName = null;
    private int mBucketId;
    private int mNumColumns;
    private int mItemsAdded = 0;
    private ArrayList<GalleryDetailsCell> mDetailsCells;
    private CursorTaskComplete mTaskComplete;

    public GalleryCursorTask(Context context, String bucketName, int bucketId, int numColumns) {
        mContext = context;
        mBucketName = bucketName;
        mBucketId = bucketId;
        mNumColumns = numColumns;
        mTaskComplete = ((GalleryDetailsActivity) context).getTaskComplete();


    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (mBucketName != null)
            mCursor = createBucketCursor(mBucketName);

        int image_column_index;
        int data_column_index;
        long id;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;
        options.outWidth = 120;
        options.outHeight = 120;
        String thumbPath = null;
        String actualImagePath = null;
        boolean bitmapGenerated = false;
        data_column_index = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        image_column_index = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
        mCursor.moveToFirst();
        if (mCursor != null && mCursor.getCount() > 0) {
            do {

                actualImagePath = mCursor.getString(data_column_index);

                id = mCursor.getLong(image_column_index);


                try {
                    try {
                        thumbPath = null;
                        thumbPath = getThumbnailPath(id, mContext, actualImagePath);
                        //Log.v("thumbPath", "" + thumbPath);
                    } catch (CursorIndexOutOfBoundsException e) {

                        MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver
                                (), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
                        thumbPath = null;
                        thumbPath = getThumbnailPath(id, mContext, actualImagePath);
                        //Log.v("ExceptionthumbPath", "" + thumbPath);
                    }

                } catch (Exception e) {
                    e.getLocalizedMessage();
                }
                if (isCancelled()) {
                    mCursor.close();
                    mTaskComplete = null;
                    mContext = null;
                    mBucketName = null;
                    break;
                }
                if (actualImagePath != null) {
                    publishProgress(new GalleryDetailsCell(actualImagePath, thumbPath));
                }

            } while (mCursor.moveToNext());
            mCursor.close();
        }
        return null;
    }

    private String getThumbnailPath(long id, Context context, String actualImage) {
        String path = null;
        boolean val = false;

        try {
            Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail
                    (context.getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (cursor.getCount() <= 0) {
                val = true;
                MediaStore.Images.Thumbnails.getThumbnail(mContext
                        .getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                //Log.v("IMAGE inserted:", "MediaStore");
            }
            if (val == true) {
                val = false;

                Cursor cursor1 = MediaStore.Images.Thumbnails.queryMiniThumbnail
                        (context.getContentResolver(), id,
                                MediaStore.Images.Thumbnails.MINI_KIND, null);
                while (cursor1 != null && cursor1.moveToFirst()) {
                    path = cursor1.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    break;
                }
                cursor1.close();
            } else {
                while (cursor != null) {
                    cursor.moveToFirst();
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    break;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;

    }

    @Override
    protected void onProgressUpdate(GalleryDetailsCell... values) {
        super.onProgressUpdate(values);
        ++mItemsAdded;
        if (mDetailsCells == null) mDetailsCells = new ArrayList<>();
        mDetailsCells.add(values[0]);
        if (mTaskComplete != null && mContext != null && mItemsAdded > 0 && mItemsAdded % mNumColumns
                == 0) {
            mTaskComplete.itenAdded(mDetailsCells);
            mItemsAdded = 0;
            mDetailsCells.clear();
        } else {

        }

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mTaskComplete != null && mContext != null) {
            //Log.v("onPostExecute Items   :", "" + mItemsAdded);
            mTaskComplete.itenAdded(mDetailsCells);
            mDetailsCells.clear();
            mItemsAdded = 0;
        }

        if (mTaskComplete != null) mTaskComplete = null;
        mContext = null;
    }

    private Cursor createBucketCursor(String bucketName) {
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        String searchParams = "bucket_display_name = \"" + bucketName + "\"";


        Cursor mPhotoCursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                searchParams, null, orderBy + " DESC");

        return mPhotoCursor;

    }
}
