package com.example.user.customgalleryfinal.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.customgalleryfinal.Models.GalleryDetailsCell;
import com.example.user.customgalleryfinal.R;
import com.example.user.customgalleryfinal.Utils.GalleryCursorTask;
import com.example.user.customgalleryfinal.Utils.CursorTaskComplete;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ${Chandran} on 1/6/15.
 */
public class GalleryDetailsActivity extends Activity implements GridView.OnItemClickListener, CursorTaskComplete {
    private GridView mPhotoGrid;
    private String mBucketName;
    private int mBucketId;
    private Context mContext;
    private Display mDisplay;
    private int mGridViewWidth;
    private int mNumColumns;
    private GridViewAdapter mGridAdapter;
    private GalleryCursorTask mCursorTask = null;
    private CursorTaskComplete mTaskComplete;
    private int mColumnWidth;

    public CursorTaskComplete getTaskComplete() {

        return mTaskComplete;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        mGridViewWidth = mDisplay.getWidth();
        mBucketName = getIntent().getStringExtra("bucket_name");
        mBucketId = getIntent().getIntExtra("bucket_id", -1);
        setContentView(R.layout.activity_details);
        mPhotoGrid = (GridView) findViewById(R.id.photo_grid);
        mPhotoGrid.setOnItemClickListener(this);
        if (mGridViewWidth > 500) {
            mPhotoGrid.setNumColumns(5);
            mNumColumns = 5;
            mColumnWidth = mGridViewWidth / 5;
            mPhotoGrid.setColumnWidth(mColumnWidth);
        } else {
            mPhotoGrid.setNumColumns(3);
            mNumColumns = 3;
            mColumnWidth = mGridViewWidth / 3;
            mPhotoGrid.setColumnWidth(mColumnWidth);
        }
        mGridAdapter = new GridViewAdapter(this, mColumnWidth);
        mTaskComplete = this;
        //mNumColumns = mPhotoGrid.getNumColumns();
        Log.v("Columns", "" + mNumColumns);
        /*mPhotoGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                *//*if (i == GridView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mGridAdapter.notifyDataSetChanged();
                }*//*
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });*/
        mCursorTask = new GalleryCursorTask(this, mBucketName, mBucketId, mNumColumns);
        mCursorTask.execute();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        /*Intent passProfilePicUrlIntent = new Intent();
        passProfilePicUrlIntent.putExtra("profile_picture_url", mGridAdapter.getDetailsCell(i));
        //startActivity(passProfilePicUrlIntent);
        *//*Toast.makeText(mContext, "" + mGridAdapter.getDetailsCell(i), Toast.LENGTH_SHORT)
                .show();*/
    }

    @Override
    public void itenAdded(ArrayList<GalleryDetailsCell> mItems) {
        if (mGridAdapter != null && mItems != null && mItems.size() > 0) {
            mGridAdapter.setDetailsCells(mItems);
            if (mPhotoGrid.getAdapter() == null) {
                mPhotoGrid.setAdapter(mGridAdapter);
            }
            mGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mPhotoGrid!= null) mPhotoGrid = null;
        if (mCursorTask != null) mCursorTask.cancel(true);
        if (mGridAdapter != null) mGridAdapter = null;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private class GridViewAdapter extends BaseAdapter {
        private Context mContext;
        private ImageView mImage = null;
        private ArrayList<GalleryDetailsCell> mDetailsCells;
        private GridView.LayoutParams mImageParams;


        GridViewAdapter(Context context, int imgSize) {
            mContext = context;
            if (mDetailsCells == null) {
                mDetailsCells = new ArrayList<>();
            }
            mColumnWidth = imgSize;


            mImageParams = new GridView.LayoutParams(imgSize, imgSize);

        }

        public String getDetailsCell(int position) {
            return mDetailsCells.get(position).getImagePath();
        }

        private void setDetailsCells(ArrayList<GalleryDetailsCell> mCells) {
            mDetailsCells.addAll(mCells);
        }

        @Override
        public int getCount() {
            return mDetailsCells.size();
        }

        @Override
        public Object getItem(int i) {
            return mDetailsCells.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ImageView imageView = null;
            if (view == null) {
                imageView = (ImageView) View.inflate(mContext, R.layout.image_view_cell, null);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageParams);
            } else {
                imageView = (ImageView) view;
            }

            final String thumbs = mDetailsCells.get(i).getThumbnailPath();
            if (thumbs != null) {
                mImage = imageView;
                Picasso.with(mContext)
                        .load(new File(thumbs))
                        .placeholder(R.drawable.placeholder_photo).noFade()
                        .resize(mColumnWidth, mColumnWidth)
                        .centerCrop()
                        .into(imageView,
                                new
                                        Callback() {
                                            @Override
                                            public void onSuccess() {
                                                //Log.v("Callback","onSuccess");
                                            }

                                            @Override
                                            public void onError() {
                                                //Log.v("Callback", "onError" + thumbs);
                                                Picasso.with(mContext)
                                                        .load(new File(mDetailsCells.get(i).getImagePath()))
                                                        .placeholder(R.drawable.placeholder_photo).noFade()
                                                        .centerCrop()
                                                        .resize(mColumnWidth, mColumnWidth)
                                                        .into(mImage);
                                            }
                                        });
            } else {
                //Log.v("ThumbPath", "Nulll");
                Picasso.with(mContext)
                        .load(new File(mDetailsCells.get(i).getImagePath()))
                        .placeholder(R.drawable.placeholder_photo).noFade().centerCrop()
                        .resize(mColumnWidth, mColumnWidth).into
                        (imageView);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = getDetailsCell(i);
                    if (path != null) {
                        Intent passProfilePicUrlIntent = new Intent();
                        passProfilePicUrlIntent.putExtra("profile_picture_url", path);
                        //startActivity(passProfilePicUrlIntent);
                        Toast.makeText(mContext, "" + path, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Log.v("PathNUll","ImagePathNULL");
                    }

                }
            });
            return imageView;
        }
    }

}
