package com.droids.tamada.filemanager.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.droids.tamada.filemanager.adapter.VideoListAdapter;
import com.droids.tamada.filemanager.model.MediaFileListModel;
import com.example.satish.filemanager.R;

import java.util.ArrayList;

/**
 * Created by Satish on 28-12-2015.
 */
public class VideosListActivity extends AppCompatActivity {
    private ArrayList<MediaFileListModel> videoListModelsArray;
    private LinearLayout noMediaLayout;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);
        ListView listview = (ListView) findViewById(R.id.audio_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        noMediaLayout = (LinearLayout) findViewById(R.id.noMediaLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Videos");
        videoListModelsArray = new ArrayList<>();
        getVideoList();
        VideoListAdapter audioListAdapter = new VideoListAdapter(this, videoListModelsArray);
        listview.setAdapter(audioListAdapter);

    }

    private void getVideoList() {
        @SuppressWarnings("deprecation") final Cursor mCursor = managedQuery(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Video.Media.TITLE + ") ASC");
        Log.d("length is", "" + mCursor.getCount());
        if (mCursor.getCount() == 0)
            noMediaLayout.setVisibility(View.VISIBLE);
        if (mCursor.moveToFirst()) {
            do {
                MediaFileListModel mediaFileListModel = new MediaFileListModel();
                mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                videoListModelsArray.add(mediaFileListModel);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }
}

