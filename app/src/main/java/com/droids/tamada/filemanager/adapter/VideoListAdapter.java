package com.droids.tamada.filemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.droids.tamada.filemanager.activity.VideosListActivity;
import com.droids.tamada.filemanager.model.MediaFileListModel;
import com.example.satish.filemanager.R;

import java.util.ArrayList;

/**
 * Created by Satish on 29-12-2015.
 */
public class VideoListAdapter extends BaseAdapter {
    private final ArrayList<MediaFileListModel> mediaFileListModelsArray;
    private final Activity activity;
    private LayoutInflater layoutInflater;

    public VideoListAdapter(VideosListActivity audiosListActivity, ArrayList<MediaFileListModel> mediaFileListModelsArray) {
        this.activity = audiosListActivity;
        this.mediaFileListModelsArray = mediaFileListModelsArray;
    }

    @Override
    public int getCount() {
        return mediaFileListModelsArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaFileListModelsArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            view = layoutInflater.inflate(R.layout.media_list_item_view, null);
        TextView lblFileName = (TextView) view.findViewById(R.id.file_name);
        ImageView imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        MediaFileListModel mediaFileListModel = mediaFileListModelsArray.get(position);
        lblFileName.setText(mediaFileListModel.getFileName());
        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(mediaFileListModel.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
        imgItemIcon.setImageBitmap(bMap);
        return view;
    }
}
