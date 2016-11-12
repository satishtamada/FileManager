package com.droids.tamada.filemanager.app;

import android.app.Application;

import com.droids.tamada.filemanager.activity.MainActivity;
import com.droids.tamada.filemanager.model.MediaFileListModel;

import java.util.ArrayList;

/**
 * Created by satish on 4/2/16.
 */
public class AppController extends Application {
    private static AppController mInstance;
    private ArrayList<MediaFileListModel>  mediaFileListModelArrayList;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }


    public void setButtonBackPressed(MainActivity.ButtonBackPressListener listener) {
        MainActivity.buttonBackPressListener=listener;
    }

    public void setMediaFileListArrayList(ArrayList<MediaFileListModel> mediaFileListArrayList) {
        this.mediaFileListModelArrayList = mediaFileListArrayList;
    }

    public ArrayList<MediaFileListModel> getMediaFileListModeLArray() {
        return mediaFileListModelArrayList;
    }
}
