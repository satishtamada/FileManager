package com.example.satish.filemanager.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.satish.filemanager.R;
import com.example.satish.filemanager.adapter.InternalStorageFilesAdapter;
import com.example.satish.filemanager.model.InternalStorageFilesModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Satish on 04-12-2015.
 */
public class InternalStorageFragment extends Fragment implements InternalStorageFilesAdapter.CustomListener {
    private ListView listView;
    private ArrayList<InternalStorageFilesModel> filesModelArrayList;
    private InternalStorageFilesAdapter internalStorageFilesAdapter;
    private ImageButton btnMenu;
    private ImageButton btnSearch;
    private ImageButton btnDelete;
    private boolean isChecked = false;
    private Dialog dialog;
    private String MENU_TAG = "main";
    private String root = "/";

    public InternalStorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interanl, container, false);
        btnMenu = (ImageButton) rootView.findViewById(R.id.btn_menu);
        btnSearch = (ImageButton) rootView.findViewById(R.id.btn_search);
        btnDelete = (ImageButton) rootView.findViewById(R.id.btn_delete);
        listView = (ListView) rootView.findViewById(R.id.internal_file_list_view);
        filesModelArrayList = new ArrayList<>();
        getDirectory(root);
        internalStorageFilesAdapter = new InternalStorageFilesAdapter(filesModelArrayList, getActivity());
        internalStorageFilesAdapter.setCustomListener(this);
        listView.setAdapter(internalStorageFilesAdapter);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("here", Boolean.toString(btnMenu.getTag().equals(MENU_TAG)));
                if (btnMenu.getTag().equals(MENU_TAG))
                    mainMenu();//it will display main menu
                else
                    directoryMenu();//if will display folder menu
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InternalStorageFilesModel model = filesModelArrayList.get(position);
                File file = new File(model.getFilePath());//get the selected item path in list view
                Log.d("path is",model.getFilePath());
                if (file.isDirectory()) {//check if selected item is directory
                    if (file.canRead())//if selected directory is readable
                        getDirectory(model.getFilePath()+"/");
                    else {
                      Dialog dialog=new Dialog(getActivity());
                        dialog.setContentView(R.layout.custom_dialog_file_not_readable);
                        dialog.show();
                        TextView folderName= (TextView) dialog.findViewById(R.id.not_read_file_name);
                        folderName.setText(model.getFilePath()+" folder can't be read!");
                    }//inner if-else
                }//if
            }//onItemClick
        });

        return rootView;
    }

    private void getDirectory(String directoryPath) {
        Log.d("in get Directory", directoryPath);
        File f = new File(directoryPath);
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                InternalStorageFilesModel model = new InternalStorageFilesModel(file.getName() + "/", file.getPath(), false);
                filesModelArrayList.add(model);
            } else {
                InternalStorageFilesModel model = new InternalStorageFilesModel(file.getName(), file.getPath(), false);
                filesModelArrayList.add(model);
            }
        }
        //here i added comments
        Log.d("action","after for loop");
        internalStorageFilesAdapter = new InternalStorageFilesAdapter(filesModelArrayList, getActivity());
        Log.d("action","after internal");
        internalStorageFilesAdapter.setCustomListener(this);
        Log.d("action", "after custom");
        listView.setAdapter(internalStorageFilesAdapter);
        Log.d("action", "after set adapter");
    }

    public void mainMenu() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_main_menu_dialog);
        dialog.setTitle("Actions");
        dialog.show();
        TextView cancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView selectAll = (TextView) dialog.findViewById(R.id.btn_select_all);
        TextView deSelectAll = (TextView) dialog.findViewById(R.id.btn_de_select_all);
        TextView newFolder = (TextView) dialog.findViewById(R.id.btn_new_folder);
        //event on new folder
        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the main menu dialog
                dialog.cancel();
                final Dialog fileDialog = new Dialog(getActivity());
                fileDialog.setContentView(R.layout.custom_new_folder_dialog);//display custom file menu
                fileDialog.setTitle("Create Folder");
                fileDialog.show();
                final EditText txtNewFolder = (EditText) fileDialog.findViewById(R.id.txt_new_folder);
                TextView create = (TextView) fileDialog.findViewById(R.id.btn_create);
                TextView cancel = (TextView) fileDialog.findViewById(R.id.btn_cancel);
                //create file event
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folderName = txtNewFolder.getText().toString();

                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileDialog.cancel();
                    }
                });

            }
        });
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = true;
                changeCheckboxStatus();
                btnDelete.setVisibility(View.VISIBLE);//display the delete button on bottom of center
                btnMenu.setTag("dirmenu");
            }
        });
        deSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = false;
                changeCheckboxStatus();
                btnDelete.setVisibility(View.GONE);//disable the delete button on bottom of center
                btnMenu.setTag("main");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();//close the menu dialog
            }
        });
    }

    //if user select any directory menu display directoryMenu
    public void directoryMenu() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dir_menu_dialog);
        dialog.setTitle("Actions");
        dialog.show();
        TextView cancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        TextView selectAll = (TextView) dialog.findViewById(R.id.btn_select_all);
        TextView deSelectAll = (TextView) dialog.findViewById(R.id.btn_de_select_all);
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = true;
                changeCheckboxStatus();
                btnDelete.setVisibility(View.VISIBLE);

            }
        });
        deSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = false;
                changeCheckboxStatus();
                btnDelete.setVisibility(View.GONE);
                btnMenu.setTag("main");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }


    public void changeCheckboxStatus() {
        for (int i = 0; i < filesModelArrayList.size(); i++) {
            InternalStorageFilesModel fileModel = filesModelArrayList.get(i);//get the all filemodel elements
            fileModel.setSelected(isChecked);//set the is checked value by getting from the selected or deselected btn
            filesModelArrayList.set(i, fileModel);//replace the element on arraylist
        }
        internalStorageFilesAdapter.notifyDataSetChanged();//set notify to list adapter
        dialog.cancel();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void isCheckboxSelectedListener(int position, boolean isChecked) {
        InternalStorageFilesModel model = filesModelArrayList.get(position);
        model.setSelected(isChecked);
        filesModelArrayList.remove(position);
        filesModelArrayList.add(position, model);
        internalStorageFilesAdapter.notifyDataSetChanged();
        if (isChecked) {//if checkbox is selected change menu to dir menu and display the delete icon
            btnMenu.setTag("dirmenu");
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnMenu.setTag("menu");//if checkbox is not selected change menu to main menu and disappear the delete icon
            btnDelete.setVisibility(View.GONE);
        }//end of else
    }
}
