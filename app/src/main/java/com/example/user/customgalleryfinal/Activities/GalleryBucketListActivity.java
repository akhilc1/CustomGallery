package com.example.user.customgalleryfinal.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.customgalleryfinal.Models.GalleryBucketCell;
import com.example.user.customgalleryfinal.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class GalleryBucketListActivity extends Activity implements ListView.OnItemClickListener {

    private ListView mBucketList;
    private ArrayList<GalleryBucketCell> mBucketCells;
    private BucketListAdapter mBucketsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mBucketList = (ListView) findViewById(R.id.bucket_list);
        mBucketList.setOnItemClickListener(this);
        setAlbumList();
    }

    private void setAlbumList() {
        new GetPhotoBucketsTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String bucketName = mBucketCells.get(i).getBucketName();
        int bucketId = mBucketCells.get(i).getBucketId();
        Intent detailsActivityIntent = new Intent(GalleryBucketListActivity.this, GalleryDetailsActivity.class);
        detailsActivityIntent.putExtra("bucket_name", bucketName);
        detailsActivityIntent.putExtra("bucket_id", bucketId);
        startActivity(detailsActivityIntent);
    }

    private class GetPhotoBucketsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mBucketCells = new ArrayList<>();

            String[] PROJECTION_BUCKET = {MediaStore.Images.ImageColumns.BUCKET_ID,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.DATA};
            String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
            String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            Cursor cur = getContentResolver().query(images, PROJECTION_BUCKET,
                    BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

            Log.v("ListingImages", " query count=" + cur.getCount());

            GalleryBucketCell album = null;

            if (cur.moveToFirst()) {
                String bucket;
                String date;
                int bucketId;
                String thumbnailPath;
                int bucketColumn = cur
                        .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int dateColumn = cur
                        .getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                int bucketIdColumn = cur
                        .getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                do {
                    bucket = cur.getString(bucketColumn);
                    date = cur.getString(dateColumn);
                    thumbnailPath = cur.getString(dataColumn);
                    bucketId = cur.getInt(bucketIdColumn);

                    if (bucket != null && bucket.length() > 0) {
                        if (album == null) album = new GalleryBucketCell();
                        album.setBucketId(bucketId);
                        album.setBucketName(bucket);
                        album.setDateTaken(date);
                        album.setThumbnailPath(thumbnailPath);
                        album.setTotalCount(photoCountByAlbum(bucket));
                        mBucketCells.add(album);
                        album = null;
                    }

                } while (cur.moveToNext());
            }
            cur.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mBucketCells.size() > 0) {
                mBucketsAdapter = new BucketListAdapter(GalleryBucketListActivity.this, mBucketCells);
                mBucketList.setAdapter(mBucketsAdapter);
            }

        }
    }

    private int photoCountByAlbum(String bucketName) {
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;

            String bucket = bucketName;
            String searchParams = "bucket_display_name = \"" + bucket + "\"";
            Cursor mPhotoCursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    searchParams, null, orderBy + " DESC");

            if (mPhotoCursor.getCount() > 0) {
                return mPhotoCursor.getCount();
            }
            mPhotoCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;

    }

    public class BucketListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<GalleryBucketCell> mNBucketCells;

        public BucketListAdapter(Context context, ArrayList<GalleryBucketCell> nCells) {
            mContext = context;
            mNBucketCells = nCells;
        }

        @Override
        public int getCount() {
            return mNBucketCells.size();
        }

        @Override
        public Object getItem(int i) {
            return mNBucketCells.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.bucket_cell_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.mThumbnail = (ImageView) convertView.findViewById(R.id.thumbnail_image);
                viewHolder.mBucketName = (TextView) convertView.findViewById(R.id.bucket_name);
                viewHolder.mPhotosCount = (TextView) convertView.findViewById(R.id.bucket_image_count);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String imagePath = mNBucketCells.get(i).getThumbnailPath();
            try {
                String bucketName = mNBucketCells.get(i).getBucketName();
                viewHolder.mBucketName.setText(bucketName.substring(0, 1).toUpperCase() + bucketName
                        .substring(1));
                Picasso.with(mContext).load(new File(imagePath)).fit().centerCrop()
                        .into(viewHolder.mThumbnail);
                viewHolder.mPhotosCount.setText("(" + mNBucketCells.get(i).getTotalCount() + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        private class ViewHolder {
            private ImageView mThumbnail;
            private TextView mBucketName;
            private TextView mPhotosCount;
        }

    }
}
