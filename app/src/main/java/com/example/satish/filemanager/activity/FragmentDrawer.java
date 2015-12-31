package com.example.satish.filemanager.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.satish.filemanager.R;
import com.example.satish.filemanager.adapter.NavigationDrawerAdapter;
import com.example.satish.filemanager.model.NavDrawerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Satish on 04-12-2015.
 */
public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();
    private static String[] titles = null;
    private static int[] icons = {R.mipmap.ic_internal_storage, R.mipmap.ic_external_storage};
    private ImageButton imgBtnSettings;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private FragmentDrawerListener drawerListener;
    private LinearLayout imagesLayout, audiosLayout, videosLayout;
    private ProgressBar internal_progress, external_progress;
    private TextView lbl_free_internal_memory, lbl_total_internal_memory, lbl_free_external_memory, lbl_total_external_memory, lbl_ram_size, lbl_ram_free_size;
    static long totalSize;
    static long freeSize;

    public FragmentDrawer() {

    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setIcon(icons[i]);
            data.add(navItem);
        }
        return data;
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        imgBtnSettings = (ImageButton) layout.findViewById(R.id.img_btn_settings);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        audiosLayout = (LinearLayout) layout.findViewById(R.id.layout_audios);
        videosLayout = (LinearLayout) layout.findViewById(R.id.layout_videos);
        imagesLayout = (LinearLayout) layout.findViewById(R.id.layout_images);
        internal_progress = (ProgressBar) layout.findViewById(R.id.progressbar1);//internal memory status
        external_progress = (ProgressBar) layout.findViewById(R.id.progressbar2);//external memory status
        lbl_free_external_memory = (TextView) layout.findViewById(R.id.free_external_memory);
        lbl_free_internal_memory = (TextView) layout.findViewById(R.id.free_internal_memory);
        lbl_total_external_memory = (TextView) layout.findViewById(R.id.total_external_memory);
        lbl_total_internal_memory = (TextView) layout.findViewById(R.id.total_internal_memory);
        lbl_ram_size = (TextView) layout.findViewById(R.id.total_ram_memory);
        lbl_ram_free_size = (TextView) layout.findViewById(R.id.free_ram_memory);
        lbl_ram_free_size.setText(getRamUsageSize() + "/");
        lbl_ram_size.setText(getRamMemorySize());
        lbl_free_internal_memory.setText(getAvailableInternalMemorySize() + "/");
        lbl_total_internal_memory.setText(getTotalInternalMemorySize());
        if (!Environment.isExternalStorageRemovable()) {//if external storage not available
            lbl_free_external_memory.setText("0MB/");//
            lbl_total_external_memory.setText("0GB");
            external_progress.setProgress(1);
            external_progress.setMax(100);
        } else {
            lbl_free_external_memory.setText(getAvailableExternalMemorySize() + "/");
            lbl_total_external_memory.setText(getTotalExternalMemorySize());
            external_progress.setProgress((int) totalSize);   // Main Progress
            external_progress.setMax((int) freeSize);
        }
        internal_progress.setProgress((int) totalSize);   // Main Progress
        internal_progress.setMax((int) freeSize); // Maximum Progress
        // Maximum Progress

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        imgBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                getActivity().startActivity(intent);
            }
        });
        audiosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AudiosListActivity.class);
                getActivity().startActivity(intent);
            }
        });
        videosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), VideosListActivity.class);
                getActivity().startActivity(intent);
            }
        });
        imagesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ImagesListActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return layout;
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize, "free");
        } else {
            return "0";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize, "total");
        } else {
            return "0";
        }
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.d("getPath", path.getPath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize, "free");
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize, "total");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public String getRamMemorySize() {
        ActivityManager actManager = (ActivityManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            totalMemory = memInfo.totalMem;
        }
        return formatSize(totalMemory, "ram");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public String getRamUsageSize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        return formatSize(availableMegs, "ramfree");
    }

    public static String formatSize(long size, String tag) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (tag.equals("total"))//set progress bar
                    totalSize = size;
                if (tag.equals("free"))
                    freeSize = size;
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

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }
}