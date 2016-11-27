package com.droids.tamada.filemanager.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.droids.tamada.filemanager.app.AppController;
import com.droids.tamada.filemanager.fragments.AudiosListFragment;
import com.droids.tamada.filemanager.fragments.ExternalStorageFragment;
import com.droids.tamada.filemanager.fragments.ImagesListFragment;
import com.droids.tamada.filemanager.fragments.InternalStorageFragment;
import com.droids.tamada.filemanager.fragments.SettingsFragment;
import com.droids.tamada.filemanager.fragments.VideosListFragment;
import com.droids.tamada.filemanager.helper.ArcProgress;
import com.droids.tamada.filemanager.helper.PreferManager;
import com.example.satish.filemanager.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_INTERNAL_STORAGE = "INTERNAL STORAGE";
    private static final String TAG_EXTERNAL_STORAGE = "EXTERNAL STORAGE";
    private static final String TAG_IMAGES_LIST = "IMAGES";
    private static final String TAG_AUDIOS_LIST = "AUDIOS";
    private static final String TAG_VIDEOS_LIST = "VIDEOS";
    private static final String TAG_SETTINGS = "SETTINGS";
    public static String FG_TAG = TAG_INTERNAL_STORAGE;
    public static int navItemIndex = 0;
    NavigationView navigationView;
    private DrawerLayout drawer;
    private String[] activityTitles;
    private Handler mHandler;
    public static ButtonBackPressListener buttonBackPressListener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArcProgress progressStorage;
    private TextView lblFreeStorage;
    private PreferManager preferManager;
    private AdView mAdView;
    private Handler handler;
    private Runnable runnable;
    private int i = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mHandler = new Handler();
        preferManager = new PreferManager(AppController.getInstance().getApplicationContext());
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                loadHomeFragment();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        progressStorage = (ArcProgress) headerLayout.findViewById(R.id.progress_storage);
        lblFreeStorage = (TextView) headerLayout.findViewById(R.id.id_free_space);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navItemIndex = 0;
            FG_TAG = TAG_INTERNAL_STORAGE;
            navigationView.getMenu().getItem(0).setChecked(true);
            loadHomeFragment();
            setRamStorageDetails(navItemIndex);
            if (preferManager.isFirstTimeLaunch()) {
                final Dialog homeGuideDialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                homeGuideDialog.setContentView(R.layout.custom_guide_dialog);
                homeGuideDialog.show();
                RelativeLayout layout = (RelativeLayout) homeGuideDialog.findViewById(R.id.guide_layout);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        preferManager.setFirstTimeLaunch(false);
                        homeGuideDialog.dismiss();
                    }
                });
            }
        }
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
                if (i > -1) {
                    i--;
                } else {
                    mAdView.setVisibility(View.GONE);
                    handler.removeCallbacks(runnable);
                }
            }
        };
        runnable.run();
    }


    private void setActivityTitle() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void loadHomeFragment() {
        setActivityTitle();
        invalidateOptionsMenu();
        if (getSupportFragmentManager().findFragmentByTag(FG_TAG) != null) {
            // getSupportFragmentManager().popBackStack(FG_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            drawer.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main_internal_storage content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, FG_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new InternalStorageFragment();
            case 1:
                return new ExternalStorageFragment();
            case 2:
                return new ImagesListFragment();
            case 3:
                return new AudiosListFragment();
            case 4:
                return new VideosListFragment();
            case 5:
                return new SettingsFragment();
            default:
                return new InternalStorageFragment();
        }
    }

    private void removeFragment() {
        if (getSupportFragmentManager().findFragmentByTag(FG_TAG) == null) {
            try {
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.frame)).commit();
            } catch (Exception e) {
                AppController.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main_internal_storage, menu);
            return true;
        }
        if (navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.main_external_storage, menu);
            return true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new_folder) {
            InternalStorageFragment internalStorageFragment = (InternalStorageFragment) getSupportFragmentManager().findFragmentByTag(FG_TAG);
            if (internalStorageFragment != null) {
                internalStorageFragment.createNewFolder();
            }
            return true;
        } else if (id == R.id.action_new_file) {
            InternalStorageFragment internalStorageFragment = (InternalStorageFragment) getSupportFragmentManager().findFragmentByTag(FG_TAG);
            if (internalStorageFragment != null) {
                internalStorageFragment.createNewFile();
            }
            return true;
        } else if (id == R.id.action_new_folder_external) {
            ExternalStorageFragment externalStorageFragment = (ExternalStorageFragment) getSupportFragmentManager().findFragmentByTag(FG_TAG);
            if (externalStorageFragment != null) {
                externalStorageFragment.createNewFolder();
            }
            return true;
        } else if (id == R.id.action_new_file_external) {
            ExternalStorageFragment externalStorageFragment = (ExternalStorageFragment) getSupportFragmentManager().findFragmentByTag(FG_TAG);
            if (externalStorageFragment != null) {
                externalStorageFragment.createNewFile();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_internal_storage:
                navItemIndex = 0;
                FG_TAG = TAG_INTERNAL_STORAGE;
                setRamStorageDetails(navItemIndex);
                break;
            case R.id.nav_external_storage:
                navItemIndex = 1;
                FG_TAG = TAG_EXTERNAL_STORAGE;
                setRamStorageDetails(navItemIndex);
                break;
            case R.id.nav_images:
                navItemIndex = 2;
                FG_TAG = TAG_IMAGES_LIST;
                break;
            case R.id.nav_audios:
                navItemIndex = 3;
                FG_TAG = TAG_AUDIOS_LIST;
                break;
            case R.id.nav_videos:
                navItemIndex = 4;
                FG_TAG = TAG_VIDEOS_LIST;
                break;
            case R.id.nav_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FlyCabs");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Download  here to visit https://play.google.com/store/apps/details?id=com.droids.tamada.filemanager&hl=en ");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.nav_settings:
                navItemIndex = 5;
                FG_TAG = TAG_SETTINGS;
                break;
        }
        removeFragment();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setRamStorageDetails(int navItemIndex) {
        if (navItemIndex == 0) {
            lblFreeStorage.setText(getAvailableInternalMemorySize());
            progressStorage.setProgress(getAvailableInternalStoragePercentage());
        } else if (navItemIndex == 1) {
            lblFreeStorage.setText(getAvailableExternalMemorySize());
            progressStorage.setProgress(getAvailableExternalStoragePercentage());

        }
    }

    private int getAvailableExternalStoragePercentage() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            @SuppressWarnings("deprecation") long totalBlocks = stat.getBlockCount();
            long totalSize = totalBlocks * blockSize;
            @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
            long availableSize = availableBlocks * blockSize;
            Log.d("here is", "" + ((availableSize * 100) / totalSize));
            int size = (int) ((availableSize * 100) / totalSize);
            return 100 - size;
        } else {
            return 0;
        }
    }

    private static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.d("getPath", path.getPath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize, "free");
    }

    private static String getAvailableExternalMemorySize() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            @SuppressWarnings("deprecation") long blockSize = stat.getBlockSize();
            @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize, "free");
        } else {
            return "0";
        }
    }

    private int getAvailableInternalStoragePercentage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        @SuppressWarnings("deprecation") long totalBlocks = stat.getBlockCount();
        long totalSize = totalBlocks * blockSize;
        @SuppressWarnings("deprecation") long availableBlocks = stat.getAvailableBlocks();
        long availableSize = availableBlocks * blockSize;
        Log.d("here is", "" + ((availableSize * 100) / totalSize));
        int size = (int) ((availableSize * 100) / totalSize);
        return 100 - size;
    }

    private static String formatSize(long size, String tag) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (navItemIndex > 1) {
                navItemIndex = 0;
                FG_TAG = TAG_INTERNAL_STORAGE;
                navigationView.getMenu().getItem(0).setChecked(true);
                loadHomeFragment();
            } else {
                buttonBackPressListener.onButtonBackPressed(navItemIndex);
            }
        }
    }

    public interface ButtonBackPressListener {
        void onButtonBackPressed(int navItemIndex);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
