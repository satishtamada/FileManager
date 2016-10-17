package com.droids.tamada.filemanager.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.droids.tamada.filemanager.fragments.AudiosListFragment;
import com.droids.tamada.filemanager.fragments.ExternalStorageFragment;
import com.droids.tamada.filemanager.fragments.ImagesListFragment;
import com.droids.tamada.filemanager.fragments.InternalStorageFragment;
import com.droids.tamada.filemanager.fragments.VideosListFragment;
import com.example.satish.filemanager.R;

import com.droids.tamada.filemanager.fragments.SettingsFragment;


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
    private RelativeLayout footerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        footerLayout = (RelativeLayout) findViewById(R.id.id_layout_footer);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navItemIndex = 0;
            FG_TAG = TAG_INTERNAL_STORAGE;
            navigationView.getMenu().getItem(0).setChecked(true);
            loadHomeFragment();
        }
    }


    private void setActivityTitle() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void loadHomeFragment() {
        setActivityTitle();
        invalidateOptionsMenu();
        loadFooter();
        if (getSupportFragmentManager().findFragmentByTag(FG_TAG) != null) {
            // getSupportFragmentManager().popBackStack(FG_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            drawer.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
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

    private void loadFooter() {
        if (navItemIndex == 0 || navItemIndex == 1) {
            if (footerLayout.getVisibility() != View.VISIBLE) {
                Animation bottomToTop = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.bottom_top);
                footerLayout.startAnimation(bottomToTop);
                footerLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (footerLayout.getVisibility() != View.GONE) {
                Animation topToBottom = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.top_bottom);
                footerLayout.startAnimation(topToBottom);
                footerLayout.setVisibility(View.GONE);
            }
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
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        /*if (navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.camera, menu);
            return true;
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_internal_storage:
                navItemIndex = 0;
                FG_TAG = TAG_INTERNAL_STORAGE;
                break;
            case R.id.nav_external_storage:
                navItemIndex = 1;
                FG_TAG = TAG_EXTERNAL_STORAGE;
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (navItemIndex > 0) {
                navItemIndex = 0;
                FG_TAG = TAG_INTERNAL_STORAGE;
                navigationView.getMenu().getItem(0).setChecked(true);
                loadHomeFragment();
            } else {
                super.onBackPressed();
            }
        }
    }
}
