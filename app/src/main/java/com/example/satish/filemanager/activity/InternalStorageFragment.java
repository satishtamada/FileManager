package com.example.satish.filemanager.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.satish.filemanager.R;
import com.example.satish.filemanager.adapter.InternalStorageFilesAdapter;
import com.example.satish.filemanager.model.InternalStorageFilesModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    private String fileExtension;
    private String selectedFileRootPath;
    private List<String> selectedFilePositions = new ArrayList<String>();

    //generate conflicts
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
        //set event on delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                // Setting Dialog Message
                alertDialog.setTitle("Delete Folder");
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
                //display confirm dialog for delete file or folder
                alertDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            File deleteFile = new File(selectedFilePath);//create file for selected file
                            boolean isDeleteFile = deleteFile.delete();//delete the file from memory
                            Log.d("delete file", Boolean.toString(isDeleteFile));
                            if (isDeleteFile) {
                                InternalStorageFilesModel model = filesModelArrayList.get(selectedFilePosition);
                                filesModelArrayList.remove(model);//remove file from listview
                                internalStorageFilesAdapter.notifyDataSetChanged();//refresh the adapter
                                btnMenu.setTag(MENU_TAG);//set menu tag for display main menu
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
            }
        });
        //set event on menu button
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnMenu.getTag().equals(MENU_TAG))//check menu tag is main menu
                    mainMenu();//it will display main menu
                else
                    directoryMenu();//if will display folder menu
            }
        });
        //set event on list item long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                InternalStorageFilesModel model = filesModelArrayList.get(position);
                model.setSelected(true);//set true value for selected item
                filesModelArrayList.remove(position);//remove the current selected item from list
                filesModelArrayList.add(position, model);//add the updated item to list
                internalStorageFilesAdapter.notifyDataSetChanged();//refresh the listview
                btnDelete.setVisibility(View.VISIBLE);//display the delete button
                btnMenu.setTag("dirmenu");//set menu tag as directory menu
                return true;
            }
        });
        //event on item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InternalStorageFilesModel model = filesModelArrayList.get(position);
                File file = new File(model.getFilePath());//get the selected item path in list view
                fileExtension = model.getFileName().substring(model.getFileName().lastIndexOf(".") + 1);
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
                //if file is not a directory
                else if (fileExtension.equals("png") || fileExtension.equals("jpeg")) {//if file type is image
                    Intent imageIntent = new Intent(getActivity().getApplicationContext(), ImageViewActivity.class);
                    imageIntent.putExtra("imagePath", model.getFilePath());
                    imageIntent.putExtra("imageName", model.getFileName());
                    getActivity().startActivity(imageIntent);
                } else if (fileExtension.equals("mp3")) {//if file type is audio
                    getAudioPlayer(model.getFileName());
                } else if (fileExtension.equals("txt") || fileExtension.equals("html") || fileExtension.equals("xml")) {//if file type is text
                    Intent txtIntent = new Intent(getActivity().getApplicationContext(), TextFileViewActivity.class);
                    txtIntent.putExtra("filePath", model.getFilePath());
                    txtIntent.putExtra("fileName", model.getFileName());
                    getActivity().startActivity(txtIntent);
                } else if (fileExtension.equals("zip") || fileExtension.equals("rar")) {//if file type is zip or rar file
                    //create a alert dialog for unzip folder
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    // Setting Dialog Message
                    alertDialog.setTitle("Unzip Folder");
                    alertDialog.setIcon(R.mipmap.ic_unzip);
                    alertDialog.setMessage(getActivity().getApplicationContext().getString(R.string.msg_prompt_unzip_folder));
                    alertDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO on dialog cancel button
                        }
                    });
                    alertDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String filePath = model.getFilePath();//zip file path
                            String outputFolder = model.getFilePath().substring(0, model.getFilePath().lastIndexOf('.'));//unzip folder
                            getUnZipDirectory(filePath, outputFolder);
                        }
                    });
                    alertDialog.show();

                } else {
                    //TODO
                }
            }//onItemClick
        });

        return rootView;
    }

    private void getUnZipDirectory(String zipFile, String outputFolder) {
        byte[] buffer = new byte[1024];
        try {
            //create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String unzipFileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + unzipFileName);
                Log.d("file unzip : ", newFile.getAbsoluteFile().getPath());
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void getAudioPlayer(String fileName) {
        Dialog dialogMusicPlayer = new Dialog(getActivity());
        dialogMusicPlayer.setContentView(R.layout.custom_dialog_music_player);
        dialogMusicPlayer.setTitle(fileName);
        dialogMusicPlayer.show();
        SeekBar seekBar = (SeekBar) dialogMusicPlayer.findViewById(R.id.volume_bar);
        final TextView startTime = (TextView) dialogMusicPlayer.findViewById(R.id.lbl_start_time);
        TextView endTime = (TextView) dialogMusicPlayer.findViewById(R.id.lbl_end_time);
        endTime.setText("" + seekBar.getMax());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                startTime.setText("" + progressChanged);
            }
        });
    }

    private void getDirectory(String directoryPath) {
        filesModelArrayList = new ArrayList<>();
        Log.d("in get Directory", directoryPath);
        File f = new File(directoryPath);
        File[] files = f.listFiles();

        if (!directoryPath.equals(root) & !directoryPath.equals("../")) {
            InternalStorageFilesModel model = new InternalStorageFilesModel("/", root, false, true);
            filesModelArrayList.add(model);
            // InternalStorageFilesModel model1 = new InternalStorageFilesModel("../", f.getParent(), false, true);
            // filesModelArrayList.add(model1);
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
        TextView refresh = (TextView) dialog.findViewById(R.id.btn_cancel);
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
                getNewFile(root);
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

    private void getNewFile(final String rootPath) {
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
                try {
                    File file = new File(rootPath + "/" + fileName + ".txt");
                    boolean isCreated = file.createNewFile();
                    if (isCreated) {
                        InternalStorageFilesModel model = new InternalStorageFilesModel(fileName + ".txt", file.getPath(), false, false);
                        filesModelArrayList.add(model);
                        internalStorageFilesAdapter.notifyDataSetChanged();
                    }
                    fileDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileDialog.cancel();
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
        TextView property = (TextView) dialog.findViewById(R.id.btn_menu_property);
        TextView newFolder = (TextView) dialog.findViewById(R.id.btn_new_folder);
        TextView reName = (TextView) dialog.findViewById(R.id.btn_rename);
        reName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                renameFile();
            }
        });
        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                getNewFolder();
            }
        });
        property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                getFileProperty(selectedFilePath, selectedFolderName);
            }
        });
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

    private void renameFile() {
        final Dialog renameDialog = new Dialog(getActivity());
        renameDialog.setContentView(R.layout.custom_dialog_rename_file);
        renameDialog.show();
        Log.d("subString", selectedFilePath.substring(0, selectedFilePath.lastIndexOf('/') + 1));
        final EditText renamed_file = (EditText) renameDialog.findViewById(R.id.txt_rename_file);
        TextView lbl_rename = (TextView) renameDialog.findViewById(R.id.btn_rename);
        TextView lbl_cancel = (TextView) renameDialog.findViewById(R.id.btn_cancel);
        renamed_file.setText(selectedFolderName);
        lbl_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File oldFile = new File(selectedFilePath);//create file with old name
                File newFile = new File(selectedFilePath.substring(0, selectedFilePath.lastIndexOf('/') + 1) + renamed_file.getText().toString());
                if (newFile.exists()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Already file exits", Toast.LENGTH_LONG).show();
                    renameDialog.cancel();
                } else {
                    boolean isRenamed = oldFile.renameTo(newFile);
                    if (isRenamed) {
                        Toast.makeText(getActivity().getApplicationContext(), selectedFilePath.substring(0, selectedFilePath.lastIndexOf('/')) + renamed_file.getText().toString(), Toast.LENGTH_SHORT).show();
                        InternalStorageFilesModel model = filesModelArrayList.get(selectedFilePosition);
                        model.setFileName(renamed_file.getText().toString());
                        model.setFilePath(newFile.getPath());
                        model.setIsDir(false);
                        model.setSelected(false);
                        filesModelArrayList.remove(selectedFilePosition);
                        filesModelArrayList.add(selectedFilePosition, model);
                        internalStorageFilesAdapter.notifyDataSetChanged();
                        btnMenu.setTag("menu");
                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "File Not Renamed", Toast.LENGTH_SHORT).show();
                    renameDialog.cancel();
                }
            }//outer else
        });
        lbl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.cancel();
            }
        });
    }

    private void getFileProperty(String selectedFilePath, String selectedFileName) {
        final Dialog propertyDialog = new Dialog(getActivity());
        propertyDialog.setContentView(R.layout.custom_dialog_file_property);
        propertyDialog.show();
        TextView lbl_file_name = (TextView) propertyDialog.findViewById(R.id.selected_file_name);
        TextView lbl_file_size = (TextView) propertyDialog.findViewById(R.id.song_size);
        TextView lbl_file_size_name = (TextView) propertyDialog.findViewById(R.id.lbl_file_size);
        TextView lblCancel = (TextView) propertyDialog.findViewById(R.id.btn_cancel);
        if (selectedFileName.equals("/") || selectedFileName.equals("../") || selectedFileName.equals("sdcard/"))
            lbl_file_size_name.setText("Used :");
        else
            lbl_file_size_name.setText("Size :");
        if (selectedFileName.equals("/"))//set label for file name
            lbl_file_name.setText("sdcard");
        else if (selectedFileName.equals("../"))
            lbl_file_name.setText("root");
        else
            lbl_file_name.setText(selectedFileName);
        lbl_file_size.setText(getTotalFileMemorySize(selectedFilePath));//set l
        lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propertyDialog.cancel();
            }
        });
    }

    private String getTotalFileMemorySize(String selectedFilePath) {
        String value = null;
        File file = new File(selectedFilePath);
        if (!file.isDirectory()) {//if selected file is not a directory
            value = formatSize(file.length());
        } else {//if selected file is directory
            value = formatSize(dirSize(file));
        }
        return value;
    }

    public static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
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

    private static long dirSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    result += dirSize(fileList[i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        }
        return 0;
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
        return formatSize(availableBlocks * blockSize, "free");
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize, "total");
    }

    public static String formatSize(long size, String tag) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024 & tag.equals("total")) {
                    suffix = "GB";
                    size /= 1024;
                }
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
        InternalStorageFilesModel model = filesModelArrayList.get(position);
        selectedFileRootPath = root;
        root = model.getFilePath();//set the root to selected filepath
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
            btnMenu.setTag(MENU_TAG);//if checkbox is not selected change menu to main menu and disappear the delete icon
            btnDelete.setVisibility(View.GONE);
            root = selectedFileRootPath;
        }//end of else
    }
}
