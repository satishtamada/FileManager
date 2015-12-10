package com.example.satish.filemanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.satish.filemanager.R;

import java.util.List;

/**
 * Created by Satish on 04-12-2015.
 */
public class ExternalStorageFragment extends Fragment {
    private LinearLayout linearLayout;
    private ListView listView;
    public ExternalStorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_external, container, false);
        listView= (ListView) rootView.findViewById(R.id.external_files_list);
        linearLayout= (LinearLayout) rootView.findViewById(R.id.noExternalStorageLayout);
        if (!Environment.isExternalStorageRemovable())
          linearLayout.setVisibility(View.VISIBLE);
        else
        {
            //TODO display your files name in a listview
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
