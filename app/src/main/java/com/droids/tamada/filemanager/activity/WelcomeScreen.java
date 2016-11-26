package com.droids.tamada.filemanager.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import com.droids.tamada.filemanager.app.AppController;
import com.example.satish.filemanager.R;

/**
 * Created by satish on 23/10/16.
 */

public class WelcomeScreen extends AppCompatActivity {
    private Button btnTurnOn;
    private LinearLayout layoutDeniedPermissionLayout;
    private static final int REQUEST_CODE_WRITE_STORAGE = 102;
    private static final String TAG = WelcomeScreen.class.getSimpleName();
    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        layoutDeniedPermissionLayout = (LinearLayout) findViewById(R.id.id_access_permissions_layout);
        btnTurnOn= (Button) findViewById(R.id.id_btn_turn_on);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accessStorage();
        } else {
            loadScreenLockActivity();
        }
        btnTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.getInstance().trackEvent("Button turn on","Permissions allow button","FileManger lite");
                accessStorage();
            }
        });
    }

    private void loadScreenLockActivity() {
        Intent intent = new Intent(getApplicationContext(), ScreenLockActivity.class);
        startActivity(intent);
        finish();
    }

    private void accessStorage() {
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), PERMISSION_WRITE_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            boolean showRequestAgain = ActivityCompat.shouldShowRequestPermissionRationale(WelcomeScreen.this, PERMISSION_WRITE_STORAGE);
            Log.e(TAG, "showRequestAgain: " + showRequestAgain);
            if (showRequestAgain) {
                new AlertDialog.Builder(this).setMessage("Storage permission is required")
                        .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(WelcomeScreen.this, new String[]{PERMISSION_WRITE_STORAGE},
                                        REQUEST_CODE_WRITE_STORAGE);
                            }
                        }).setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layoutDeniedPermissionLayout.setVisibility(View.VISIBLE);
                    }
                }).show();
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{PERMISSION_WRITE_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
                return;
            }
        }
        loadScreenLockActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_STORAGE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        loadScreenLockActivity();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // Permission Denied
                        layoutDeniedPermissionLayout.setVisibility(View.VISIBLE);
                        SharedPreferences pref = getSharedPreferences("fileManager", 0);
                        if (!pref.getBoolean("is_camera_requested", false)) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("is_camera_requested", true);
                            editor.apply();
                            return;
                        }
                        boolean showRequestAgain = ActivityCompat.shouldShowRequestPermissionRationale(WelcomeScreen.this, PERMISSION_WRITE_STORAGE);
                        if (showRequestAgain) {
                            //true,
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Permission Required");
                            builder.setMessage("Storage Permission is required");
                            builder.setPositiveButton("DENY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("RE-TRY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(WelcomeScreen.this, new String[]{PERMISSION_WRITE_STORAGE},
                                            REQUEST_CODE_WRITE_STORAGE);
                                }
                            });
                            builder.show();
                        } else {
                            promptSettings();
                        }
                    } else {
                        Log.e(TAG, "last else");
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void promptSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage(Html.fromHtml("We require your consent to additional permission in order to proceed. Please enable them in <b>Settings</b>"));
        builder.setPositiveButton("go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // finish();
            }
        });
        builder.show();
    }

    private void goToSettings() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().trackScreenView("Welcome screen");
    }
}
