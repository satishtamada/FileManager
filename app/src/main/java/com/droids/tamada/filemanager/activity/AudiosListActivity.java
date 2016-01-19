package com.droids.tamada.filemanager.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.droids.tamada.filemanager.adapter.AudioListAdapter;
import com.droids.tamada.filemanager.helper.Utilities;
import com.droids.tamada.filemanager.model.MediaFileListModel;
import com.example.satish.filemanager.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Satish on 28-12-2015.
 */
public class AudiosListActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    private TextView startTime;
    private TextView endTime;
    private SeekBar seekBar;
    private LinearLayout noMediaLayout;
    private ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private MediaPlayer mediaPlayer;
    private Utilities utilities;
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            endTime.setText("" + utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            startTime.setText("" + utilities.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = utilities.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

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
        getSupportActionBar().setTitle("Audios");
        mediaPlayer = new MediaPlayer();
        mediaFileListModelsArray = new ArrayList<>();
        getMusicList();
        AudioListAdapter audioListAdapter = new AudioListAdapter(this, mediaFileListModelsArray);
        listview.setAdapter(audioListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaFileListModel model = mediaFileListModelsArray.get(position);
                try {
                    getAudioPlayer(model.getFileName(), model.getFilePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getAudioPlayer(String fileName, String filePath) throws IOException {
        Dialog dialogMusicPlayer = new Dialog(this);
        dialogMusicPlayer.setContentView(R.layout.custom_dialog_music_player);
        dialogMusicPlayer.setTitle(fileName);
        dialogMusicPlayer.show();
        seekBar = (SeekBar) dialogMusicPlayer.findViewById(R.id.volume_bar);
        startTime = (TextView) dialogMusicPlayer.findViewById(R.id.lbl_start_time);
        endTime = (TextView) dialogMusicPlayer.findViewById(R.id.lbl_end_time);
        final ImageButton btnPlayPause = (ImageButton) dialogMusicPlayer.findViewById(R.id.btnPlayPause);
        utilities = new Utilities();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
        updateProgressBar();
        dialogMusicPlayer.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mediaPlayer.stop();
            }
        });
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        // Changing button image to play button
                        btnPlayPause.setImageResource(R.mipmap.ic_play);
                    }
                } else {
                    // Resume song
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        // Changing button image to pause button
                        btnPlayPause.setImageResource(R.mipmap.ic_pause);
                    }
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utilities.progressToTimer(seekBar.getProgress(), totalDuration);
                // forward or backward to certain seconds
                mediaPlayer.seekTo(currentPosition);
                // update timer progress again
                updateProgressBar();
            }
        });
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private void getMusicList() {
        @SuppressWarnings("deprecation") final Cursor mCursor = managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");
        Log.d("audio list", "" + mCursor.getCount());
        if (mCursor.getCount() == 0)
            noMediaLayout.setVisibility(View.VISIBLE);
        if (mCursor.moveToFirst()) {
            do {
                MediaFileListModel mediaFileListModel = new MediaFileListModel();
                mediaFileListModel.setFileName(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                mediaFileListModel.setFilePath(mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                mediaFileListModelsArray.add(mediaFileListModel);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }
}