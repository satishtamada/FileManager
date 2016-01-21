package com.droids.tamada.filemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.droids.tamada.filemanager.activity.InternalStorageFragment;
import com.droids.tamada.filemanager.model.InternalStorageFilesModel;
import com.example.satish.filemanager.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Satish on 26-12-2015.
 */
public class InternalStorageFilesAdapter extends BaseAdapter {
    private final Activity activity;
    private final ArrayList<InternalStorageFilesModel> filesModelArrayList;
    private CustomListener customListener;
    private LayoutInflater inflater;

    public InternalStorageFilesAdapter(ArrayList<InternalStorageFilesModel> filesModelArrayList, FragmentActivity activity) {
        this.activity = activity;
        this.filesModelArrayList = filesModelArrayList;
    }

    public void setCustomListener(InternalStorageFragment customListener) {
        this.customListener = customListener;
    }

    @Override
    public int getCount() {
        return filesModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return filesModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            view = inflater.inflate(R.layout.files_item_view, null);
        TextView lblFileName = (TextView) view.findViewById(R.id.file_name);
        TextView lblFilePath = (TextView) view.findViewById(R.id.file_path);
        ImageView imgItemIcon = (ImageView) view.findViewById(R.id.icon);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        final InternalStorageFilesModel model = filesModelArrayList.get(position);
        String fileExtension = model.getFileName().substring(model.getFileName().lastIndexOf(".") + 1);


        if (model.isDir()) {//if list item folder the set icon
            imgItemIcon.setImageResource(R.mipmap.ic_folder);
        } else if (fileExtension.equals("png") || fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {//if list item any image then
            File imgFile = new File(model.getFilePath());
            if (imgFile.exists()) {
                Log.d("action", model.getFilePath());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgItemIcon.setImageBitmap(myBitmap);
            }
        } else if (fileExtension.equals("pdf")) {
            imgItemIcon.setImageResource(R.mipmap.ic_pdf);
        } else if (fileExtension.equals("mp3")) {
            imgItemIcon.setImageResource(R.mipmap.ic_mp3);
        } else if (fileExtension.equals("txt")) {
            imgItemIcon.setImageResource(R.mipmap.ic_file);
        } else if (fileExtension.equals("zip") || fileExtension.equals("rar")) {
            imgItemIcon.setImageResource(R.mipmap.ic_zip);
        } else if (fileExtension.equals("html") || fileExtension.equals("xml")) {
            imgItemIcon.setImageResource(R.mipmap.ic_html_xml);
        } else if (fileExtension.equals("mp4") || fileExtension.equals("3gp") || fileExtension.equals("wmv")) {
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(model.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            imgItemIcon.setImageBitmap(bMap);

        } else imgItemIcon.setImageResource(R.mipmap.ic_unknown_file);
        if (model.isDir())
            lblFileName.setText(model.getFileName().substring(0, model.getFileName().length() - 1));
        else lblFileName.setText(model.getFileName());
        if (model.getFileName().equals("/")) {
            lblFilePath.setText("/sdcard");
            lblFileName.setText("parent");
            imgItemIcon.setImageResource(R.mipmap.ic_parent_folder);
        } else {
            lblFilePath.setText(model.getFilePath());
        }
        if (!model.getFileName().equals("/"))//if file is not parent
            checkBox.setVisibility(View.VISIBLE);//checkbox visible
        else //if file is parent
            checkBox.setVisibility(View.INVISIBLE);//checkbox invisible
        checkBox.setChecked(model.isSelected());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.isSelected()) {
                    checkBox.setChecked(true);
                    customListener.isCheckboxSelectedListener(position, checkBox.isChecked());
                } else {
                    checkBox.setChecked(false);
                    customListener.isCheckboxSelectedListener(position, checkBox.isChecked());
                }
            }
        });
        return view;
    }

    public interface CustomListener {
        void isCheckboxSelectedListener(int position, boolean isChecked);
    }
}
