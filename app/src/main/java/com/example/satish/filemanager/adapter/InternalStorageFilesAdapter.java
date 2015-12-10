package com.example.satish.filemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.satish.filemanager.R;
import com.example.satish.filemanager.activity.InternalStorageFragment;
import com.example.satish.filemanager.model.InternalStorageFilesModel;

import java.util.ArrayList;

/**
 * Created by Satish on 05-12-2015.
 */
public class InternalStorageFilesAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private ArrayList<InternalStorageFilesModel> filesModelArrayList;
    public CustomListener customListener;

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
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        final InternalStorageFilesModel model = filesModelArrayList.get(position);
        lblFileName.setText(model.getFileName());
        if (!model.getFilePath().equals("../") || model.getFilePath().equals("/"))
            lblFilePath.setText(model.getFilePath());
        else lblFilePath.setText("hell");
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
                Toast.makeText(activity.getApplicationContext(), model.getFileName(), Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public interface CustomListener {
        void isCheckboxSelectedListener(int position, boolean isChecked);
    }
}




