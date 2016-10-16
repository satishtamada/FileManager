package com.droids.tamada.filemanager.app;

import android.app.Application;

/**
 * Created by satish on 4/2/16.
 */
public class AppController extends Application {
    private static AppController mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
}
