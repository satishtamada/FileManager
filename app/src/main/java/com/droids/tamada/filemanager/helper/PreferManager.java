package com.droids.tamada.filemanager.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by satish on 4/2/16.
 */
public class PreferManager {
    private static final String SELECTED_ADDRESS = "address";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHive";

    // All Shared Preferences Keys
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_NOTIFICATIONS_ON = "IsNotificationON";
    private static final String IS_PASSWORD_ON = "IsPasswordOn";
    private static final String KEY_PASSWORD = "password";
    private static final String IS_HIDDEN_FILE_SHOW ="isHiddenFileShow";

    public PreferManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void addNotification(String notification) {
        // get old notifications
        String oldNotifications = getNotifications();
        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }
        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
        setFirstTimeLaunch(false);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setNotificationON(boolean isNotificationOn) {
        editor.putBoolean(IS_NOTIFICATIONS_ON, isNotificationOn);
        editor.commit();
    }

    public boolean isNotificationON() {
        return pref.getBoolean(IS_NOTIFICATIONS_ON, true);
    }

    public void setPasswordActivated(boolean isPasswordOn) {
        editor.putBoolean(IS_PASSWORD_ON, isPasswordOn);
        editor.commit();
    }

    public boolean isPasswordActivated() {
        return pref.getBoolean(IS_PASSWORD_ON, false);
    }

    public void setHiddenFileVisible(boolean isHiddenFilEVisible) {
        editor.putBoolean(IS_HIDDEN_FILE_SHOW, isHiddenFilEVisible);
        editor.commit();
    }

    public boolean isHiddenFileVisible() {
        return pref.getBoolean(IS_HIDDEN_FILE_SHOW, false);
    }


    public void setPassword(String strPassword) {
        editor.putString(KEY_PASSWORD, strPassword);
        editor.commit();
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, "");
    }
}