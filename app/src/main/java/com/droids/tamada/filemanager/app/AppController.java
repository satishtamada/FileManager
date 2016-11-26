package com.droids.tamada.filemanager.app;

import android.app.Application;

import com.droids.tamada.filemanager.activity.MainActivity;
import com.droids.tamada.filemanager.helper.AnalyticsTrackers;
import com.droids.tamada.filemanager.model.MediaFileListModel;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

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
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
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

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
}
