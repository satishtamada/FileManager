package com.example.satish.filemanager.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.satish.filemanager.R;
import com.example.satish.filemanager.adapter.InternalStorageFilesAdapter;
import com.example.satish.filemanager.model.InternalStorageFilesModel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Satish on 04-12-2015.
 */
public class InternalStorageFragment extends Fragment implements InternalStorageFilesAdapter.CustomListener {
    private ListView listView;
    private ArrayList<InternalStorageFilesModel> filesModelArrayList;
    private InternalStorageFilesAdapter internalStorageFilesAdapter;
    private ImageButton btnMenu;
    private ImageButton btnDelete;
    private boolean isChecked = false;
    private Dialog dialog;
    private String MENU_TAG = "main";
    private String root = "/sdcard";
    private String selectedFilePath;
    private String selectedFolderName;
    private int selectedFilePosition;
    private List<String> selectedFilePositions = new ArrayList<String>();
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
    //creating conflicts for child
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

        btnDelete = (ImageButton) rootView.findViewById(R.id.btn_delete);
        listView = (ListView) rootView.findViewById(R.id.internal_file_list_view);

        getDirectory(root);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                // Setting Dialog Message
                alertDialog.setIcon(R.mipmap.ic_delete_folder);
                if (selectedFilePositions.size() == 1)//if user select single folder
                    alertDialog.setMessage(getActivity().getApplicationContext().getString(R.string.msg_prompt_delete_folder).replace("#name#", selectedFolderName));
                else //if user select multi folders
                    alertDialog.setMessage(getActivity().getApplicationContext().getString(R.string.msg_prompt_delete_folders));
                alertDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO on dialog cancel button
                    }
                });
                alertDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedFilePositions.size() == 1) {
                            try {
                                File deleteFile = new File(selectedFilePath);
                                boolean isDeleteFile = deleteFile.delete();
                                if (isDeleteFile) {
                                    InternalStorageFilesModel model = filesModelArrayList.get(selectedFilePosition);
                                    filesModelArrayList.remove(model);
                                    internalStorageFilesAdapter.notifyDataSetChanged();
                                    btnMenu.setTag(MENU_TAG);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            for (int i = 0; i < selectedFilePositions.size() - 1; i++) {
                                Log.d("here", i + " " + selectedFilePositions.size());
                                File deleteFile = new File(selectedFilePositions.get(i));
                                boolean isDeleteFile = deleteFile.delete();
                                if (isDeleteFile) {
                                    InternalStorageFilesModel model = filesModelArrayList.get(selectedFilePosition);
                                    selectedFilePositions.remove(model.getFilePath());
                                    filesModelArrayList.remove(model);
                                    internalStorageFilesAdapter.notifyDataSetChanged();
                                    btnMenu.setTag(MENU_TAG);
                                }//if
                            }//for
                        }//else
                    }
                });
                alertDialog.show();
                //event button request add friend
            }
        });
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
                InternalStorageFilesModel model = filesModelArrayList.get(position);
                model.setSelected(true);
                filesModelArrayList.remove(position);
                filesModelArrayList.add(position, model);
                internalStorageFilesAdapter.notifyDataSetChanged();
                btnDelete.setVisibility(View.VISIBLE);
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InternalStorageFilesModel model = filesModelArrayList.get(position);
                File file = new File(model.getFilePath());//get the selected item path in list view
                // getDirectory(model.getFilePath());
                if (file.isDirectory()) {//check if selected item is directory
                    Log.d("here ", Boolean.toString(file.isDirectory()));
                    if (file.canRead()) {//if selected directory is readable
                        Log.d("here", Boolean.toString(file.canRead()));
                        if (model.getFileName().equals("../"))//if filename root the we set dirctory path ../
                            getDirectory("../");
                        else
                            getDirectory(model.getFilePath());//if filename not root
                        root = model.getFilePath();
                    } else {
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.custom_dialog_file_not_readable);
                        dialog.show();
                        TextView folderName = (TextView) dialog.findViewById(R.id.not_read_file_name);
                        Button btnOkay = (Button) dialog.findViewById(R.id.btn_okay);
                        folderName.setText(model.getFilePath() + " folder can't be read!");
                        btnOkay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });

                    }//inner if-else
                }//if
            }//onItemClick
        });

        return rootView;
    }

    private void getDirectory(String directoryPath) {
        filesModelArrayList = new ArrayList<>();
        Log.d("in get Directory", directoryPath);
        File f = new File(directoryPath);
        File[] files = f.listFiles();

        if (!directoryPath.equals(root) & !directoryPath.equals("../")) {
            InternalStorageFilesModel model = new InternalStorageFilesModel("/", root, false, true);
            filesModelArrayList.add(model);
            InternalStorageFilesModel model1 = new InternalStorageFilesModel("../", f.getParent(), false, true);
            filesModelArrayList.add(model1);
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                InternalStorageFilesModel model = new InternalStorageFilesModel(file.getName() + "/", file.getPath(), false, true);
                filesModelArrayList.add(model);
            } else {
                InternalStorageFilesModel model = new InternalStorageFilesModel(file.getName(), file.getPath(), false, false);
                filesModelArrayList.add(model);
            }
        }
        internalStorageFilesAdapter = new InternalStorageFilesAdapter(filesModelArrayList, getActivity());
        internalStorageFilesAdapter.setCustomListener(this);
        listView.setAdapter(internalStorageFilesAdapter);

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
        TextView newFile = (TextView) dialog.findViewById(R.id.btn_new_file);
        TextView refresh= (TextView) dialog.findViewById(R.id.btn_cancel);
        final TextView property = (TextView) dialog.findViewById(R.id.btn_property);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internalStorageFilesAdapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProperties();
            }

        });
        //event on new folder
        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewFolder();
            }
        });
        newFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewFile();
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

    private void getNewFile() {
        dialog.cancel();
        final Dialog fileDialog = new Dialog(getActivity());
        fileDialog.setContentView(R.layout.custom_new_folder_dialog);//display custom file menu
        fileDialog.setTitle("Create Folder");
        fileDialog.show();
        final EditText txtNewFolder = (EditText) fileDialog.findViewById(R.id.txt_new_folder);
        TextView create = (TextView) fileDialog.findViewById(R.id.btn_create);
        TextView cancel = (TextView) fileDialog.findViewById(R.id.btn_cancel);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = txtNewFolder.getText().toString();
                String data = "";
                FileOutputStream outputStream = null;
                try {
                    outputStream = getActivity().getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                    outputStream.write(data.getBytes());
                    outputStream.close();
                    Toast.makeText(getActivity().getApplicationContext(), fileName + " saved",
                            Toast.LENGTH_LONG).show();
                    fileDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getNewFolder() {
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
                try {
                    File file = new File(root + "/" + folderName);
                    boolean isFolderCreated = file.mkdir();
                    if (isFolderCreated) {
                        InternalStorageFilesModel model = new InternalStorageFilesModel(folderName, root + "/" + folderName, false, true);
                        filesModelArrayList.add(model);
                        internalStorageFilesAdapter.notifyDataSetChanged();
                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "Folder Not Created..!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                fileDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileDialog.cancel();
            }
        });
    }

    private void getProperties() {
        dialog.cancel();
        final Dialog propertyDialog = new Dialog(getActivity());
        propertyDialog.setContentView(R.layout.custom_dialog_property);
        propertyDialog.show();
        TextView lblTotalDiskSize = (TextView) propertyDialog.findViewById(R.id.used_space);
        TextView lblFreeDiskSize = (TextView) propertyDialog.findViewById(R.id.free_space);
        TextView lblCancel = (TextView) propertyDialog.findViewById(R.id.btn_cancel);
        lblFreeDiskSize.setText(getAvailableInternalMemorySize());
        lblTotalDiskSize.setText(getTotalInternalMemorySize());
        lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propertyDialog.cancel();
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

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.d("getPath", path.getPath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }


    public static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
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
        Toast.makeText(getActivity().getApplicationContext(), "" + selectedFilePositions.size(), Toast.LENGTH_SHORT).show();
        InternalStorageFilesModel model = filesModelArrayList.get(position);
        selectedFilePath = model.getFilePath();
        selectedFolderName = model.getFileName();
        selectedFilePosition = position;
        model.setSelected(isChecked);
        filesModelArrayList.remove(position);
        filesModelArrayList.add(position, model);
        internalStorageFilesAdapter.notifyDataSetChanged();
        if (isChecked) {//if checkbox is selected change menu to dir menu and display the delete icon
            selectedFilePositions.add(selectedFilePath);
            btnMenu.setTag("dirmenu");
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            selectedFilePositions.remove(selectedFilePositions.size() - 1);
            btnMenu.setTag(MENU_TAG);//if checkbox is not selected change menu to main menu and disappear the delete icon
            if (selectedFilePositions.size() == 0)
                btnDelete.setVisibility(View.GONE);
            else btnDelete.setVisibility(View.VISIBLE);
        }//end of else
    }
}
