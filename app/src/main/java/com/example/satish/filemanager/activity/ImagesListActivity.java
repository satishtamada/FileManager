package com.example.satish.filemanager.activity;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.satish.filemanager.R;
import com.example.satish.filemanager.adapter.ImagesListAdapter;
import com.example.satish.filemanager.model.MediaFileListModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Satish on 28-12-2015.
 */
public class ImagesListActivity extends AppCompatActivity {
    private ArrayList<MediaFileListModel> imageListModelsArray;
    private Toolbar toolbar;
    private ImagesListAdapter audioListAdapter;
    private ListView listview;
    private LinearLayout noMediaLayout;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        listview = (ListView) findViewById(R.id.audio_listview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        noMediaLayout = (LinearLayout) findViewById(R.id.noMediaLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Images");
        imageListModelsArray = new ArrayList<>();
        getImagesList();
        audioListAdapter = new ImagesListAdapter(this, imageListModelsArray);
        listview.setAdapter(audioListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaFileListModel model = imageListModelsArray.get(position);
                getImageView(model.getFilePath(), model.getFileName());
            }
        });

    }

    private void getImageView(String filePath, String fileName) {
        Dialog dialogImageView = new Dialog(this);
        dialogImageView.setContentView(R.layout.custom_dialog_image_view);
        dialogImageView.setTitle(fileName);
        dialogImageView.show();
        ImageView imageView = (ImageView) dialogImageView.findViewById(R.id.image_file);
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }

    private void getImagesList() {
        final Cursor mCursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Images.Media.TITLE + ") ASC");
        Log.d("images list",""+mCursor.getCount());
        if (mCursor.getCount() == 0)
            noMediaLayout.setVisibility(View.VISIBLE);
        if (mCursor.moveToFirst()) {
            do {
                MediaFileListModel mediaFileListModel = new MediaFileListModel();
                mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                imageListModelsArray.add(mediaFileListModel);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }
}