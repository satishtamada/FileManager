package com.droids.satish.filemanager.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.droids.satish.filemanager.adapter.ExpandableListAdapter;
import com.droids.satish.filemanager.model.TextEditorOptionsModel;
import com.example.satish.filemanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Satish on 10-12-2015.
 */
public class SettingsActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    String child = null;
    TextEditorOptionsModel textEditorOptionsModel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        textEditorOptionsModel = new TextEditorOptionsModel();
        // preparing list data
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                child = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                if (child.equals("Change font size")) {
                    textEditorOptionsModel.setTextSize(30);
                }
                return false;
            }
        });
        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Listview Group collapsed listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("General settings");
        listDataHeader.add("Storage options");
        listDataHeader.add("Editor options");
        listDataHeader.add("About");

        // Adding child data
        List<String> generalSettings = new ArrayList<String>();
        generalSettings.add("Set password");
        generalSettings.add("Show hidden files");

        List<String> storageOptions = new ArrayList<String>();
        storageOptions.add("Set password");
        storageOptions.add("Formant Disk");

        List<String> editorOptions = new ArrayList<String>();
        editorOptions.add("Change text color");
        editorOptions.add("Change editor background");
        editorOptions.add("Change font size");

        List<String> about = new ArrayList<String>();
        about.add("FileManager 2.5 " + "\t" + "Created by Tamada Satish");

        listDataChild.put(listDataHeader.get(0), generalSettings); // Header, Child data
        listDataChild.put(listDataHeader.get(1), storageOptions);
        listDataChild.put(listDataHeader.get(2), editorOptions);
        listDataChild.put(listDataHeader.get(3), about);
    }
}
