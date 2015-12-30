package com.example.satish.filemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.satish.filemanager.R;
import com.example.satish.filemanager.activity.AudiosListActivity;
import com.example.satish.filemanager.model.AudioListModel;

import java.util.ArrayList;

/**
 * Created by Satish on 29-12-2015.
 */
public class AudioListAdapter extends BaseAdapter {
    private ArrayList<AudioListModel> audioListModelsArray;
    private Activity activity;
    private LayoutInflater layoutInflater;

    public AudioListAdapter(AudiosListActivity audiosListActivity, ArrayList<AudioListModel> audioListModelsArray) {
        this.activity = audiosListActivity;
        this.audioListModelsArray = audioListModelsArray;
    }

    @Override
    public int getCount() {
        return audioListModelsArray.size();
    }

    @Override
    public Object getItem(int position) {
        return audioListModelsArray.get(position);
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
            view = layoutInflater.inflate(R.layout.audio_list_item_view, null);
        TextView lblFileName = (TextView) view.findViewById(R.id.file_name);
        ImageView imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        AudioListModel audioListModel = audioListModelsArray.get(position);
        lblFileName.setText(audioListModel.getAudio_name());
        imgItemIcon.setImageResource(R.mipmap.ic_mp3);
        return view;
    }
}
