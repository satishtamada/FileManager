package com.droids.tamada.filemanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.droids.tamada.filemanager.model.MediaFileListModel;
import com.example.satish.filemanager.R;

import java.util.List;

/**
 * Created by satish on 16/10/16.
 */

public class AudiosListAdapter extends RecyclerView.Adapter<AudiosListAdapter.MyViewHolder> {
    private List<MediaFileListModel> mediaFileListModels;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblFileName,lblFileSize,lblFileCreated;
        public ImageView imgItemIcon;

        public MyViewHolder(View view) {
            super(view);
            lblFileName = (TextView) view.findViewById(R.id.file_name);
            lblFileCreated= (TextView) view.findViewById(R.id.file_created);
            imgItemIcon = (ImageView) view.findViewById(R.id.icon);
            lblFileSize= (TextView) view.findViewById(R.id.file_size);
        }
    }

    public AudiosListAdapter(List<MediaFileListModel> mediaFileListModels) {
        this.mediaFileListModels = mediaFileListModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_list_item_view, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        MediaFileListModel mediaFileListModel = mediaFileListModels.get(position);
        holder.lblFileName.setText(mediaFileListModel.getFileName());
        holder.lblFileSize.setText(mediaFileListModel.getFileSize());
        holder.lblFileCreated.setText(mediaFileListModel.getFileCreatedTime().substring(0,19));
        holder.imgItemIcon.setImageResource(R.drawable.ic_audio_file);
    }

    @Override
    public int getItemCount() {
        return mediaFileListModels.size();
    }
}
