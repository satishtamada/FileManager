package com.droids.tamada.filemanager.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.droids.tamada.filemanager.model.InternalStorageFilesModel;
import com.example.satish.filemanager.R;

import java.io.File;
import java.util.List;

/**
 * Created by satish on 17/10/16.
 */

public class InternalStorageListAdapter extends RecyclerView.Adapter<InternalStorageListAdapter.MyViewHolder> {
    private List<InternalStorageFilesModel> internalStorageFilesModels;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblFileName;
        public ImageView imgItemIcon;

        public MyViewHolder(View view) {
            super(view);
            lblFileName = (TextView) view.findViewById(R.id.file_name);
            imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        }
    }

    public InternalStorageListAdapter(List<InternalStorageFilesModel> internalStorageFilesModels) {
        this.internalStorageFilesModels = internalStorageFilesModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_list_item_view, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        InternalStorageFilesModel mediaFileListModel = internalStorageFilesModels.get(position);
        holder.lblFileName.setText(mediaFileListModel.getFileName());
        String fileExtension = mediaFileListModel.getFileName().substring(mediaFileListModel.getFileName().lastIndexOf(".") + 1);
        File file = new File(mediaFileListModel.getFilePath());
        if (file.isDirectory()) {//if list item folder the set icon
            holder.imgItemIcon.setImageResource(R.drawable.ic_folder);
        } else if (fileExtension.equals("png") || fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {//if list item any image then
            File imgFile = new File(mediaFileListModel.getFilePath());
            if (imgFile.exists()) {
                Log.d("action", mediaFileListModel.getFilePath());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imgItemIcon.setImageBitmap(myBitmap);
            }
        } else if (fileExtension.equals("pdf")) {
            holder.imgItemIcon.setImageResource(R.mipmap.ic_pdf);
        } else if (fileExtension.equals("mp3")) {
            holder.imgItemIcon.setImageResource(R.mipmap.ic_mp3);
        } else if (fileExtension.equals("txt")) {
            holder.imgItemIcon.setImageResource(R.mipmap.ic_file);
        } else if (fileExtension.equals("zip") || fileExtension.equals("rar")) {
            holder.imgItemIcon.setImageResource(R.mipmap.ic_zip);
        } else if (fileExtension.equals("html") || fileExtension.equals("xml")) {
            holder.imgItemIcon.setImageResource(R.mipmap.ic_html_xml);
        } else if (fileExtension.equals("mp4") || fileExtension.equals("3gp") || fileExtension.equals("wmv")) {
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(mediaFileListModel.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            holder.imgItemIcon.setImageBitmap(bMap);

        }else if(fileExtension.equals("apk")){
            holder.imgItemIcon.setImageResource(R.drawable.ic_apk);
        }
    }

    @Override
    public int getItemCount() {
        return internalStorageFilesModels.size();
    }
}