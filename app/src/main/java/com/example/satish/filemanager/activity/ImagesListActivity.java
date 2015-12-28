package com.example.satish.filemanager.activity;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.satish.filemanager.R;

/**
 * Created by Satish on 28-12-2015.
 */
public class ImagesListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter mAdapter;

    private final int THUMBNAIL_LOADER_ID = 0;
    private final int IMAGE_LOADER_ID = 1;

    MatrixCursor mMatrixCursor;
    Cursor mThumbCursor;
    Cursor mImageCursor;

    String mThumbImageId = "";
    String mThumbImageData = "";
    String mImageSize = "";
    String mImageTitle = "";
    String mImageWidth = "";
    String mImageHeight = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageslist);

        /** Initializing MatrixCursor */
        mMatrixCursor = new MatrixCursor(new String[]{"_id", "_data", "_details"});

        /** Getting a reference to listview of the MainActivity layout */
        ListView lVThumbnail = (ListView) findViewById(R.id.lv_thumbnail);

        /** Creating an adapter object to set image and text in the listview */
        mAdapter = new SimpleCursorAdapter(
                getBaseContext(),
                R.layout.images_list_item,
                null,
                new String[]{"_data", "_details"},
                new int[]{R.id.img_thumbnail, R.id.tv_details},
                0
        );

        /** Setting adapter for the listview */
        lVThumbnail.setAdapter(mAdapter);

        /** Loader to get images from the SD Card */
        getSupportLoaderManager().initLoader(THUMBNAIL_LOADER_ID, null, this);

    }


    /**
     * A callback method invoked by the loader when initLoader() is called
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle arg1) {
        CursorLoader cLoader;
        Uri uri;

        if (loader_id == THUMBNAIL_LOADER_ID) {    /** Querying Thumbnail content provider */
            uri = MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI;
            cLoader = new CursorLoader(this, uri, null, null, null, null);
        } else { /** Querying Image Content provider with thumbnail image id */
            String image_id = arg1.getString("image_id");
            uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            cLoader = new CursorLoader(this, uri, null, MediaStore.Images.Media._ID + "=" + image_id, null, null);
        }

        return cLoader;
    }

    /**
     * A callback method, invoked after the requested content provider returned all the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {

        if (arg0.getId() == THUMBNAIL_LOADER_ID) {  /** Thumbnail cursor is loaded completely */
            mThumbCursor = arg1;
            if (mThumbCursor.moveToFirst()) { /** Taking the first thumbnail */
                mThumbImageId = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
                mThumbImageData = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                /** Getting the image id from the mThumbCursor and putting in to the bundle*/
                String image_id = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                Bundle data = new Bundle();
                data.putString("image_id", image_id);
                /** Intiates the Image Loader onCreateLoader() */
                getSupportLoaderManager().initLoader(IMAGE_LOADER_ID, data, this);
            }

        } else if (arg0.getId() == IMAGE_LOADER_ID) {
            mImageCursor = arg1;
            if (mImageCursor.moveToFirst()) {
                mImageTitle = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                mImageSize = mImageCursor.getString(mImageCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                mImageWidth = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.WIDTH));
                mImageHeight = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.HEIGHT));
                String details = "Title:" + mImageTitle + "\n" +
                        "Size:" + mImageSize + " Bytes " + "\n" +
                        "Resolution:" + mImageWidth + " x " + mImageHeight;
                /** Adding new row to the matrixcursor object */
                mMatrixCursor.addRow(new Object[]{mThumbImageId, mThumbImageData, details});
                /** Taking the next thumbnail */
                if (mThumbCursor.moveToNext()) {
                    mThumbImageId = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
                    mThumbImageData = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    String image_id = mThumbCursor.getString(mThumbCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                    Bundle data = new Bundle();
                    data.putString("image_id", image_id);
                    /** Restarting the image loader to get the next image details */
                    getSupportLoaderManager().restartLoader(IMAGE_LOADER_ID, data, this);
                } else { /** No more thumbnails exists */
                    if (mThumbCursor.isAfterLast())
                        mAdapter.swapCursor(mMatrixCursor); /** Set the thumbnails and its details in the listview */
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }
}
