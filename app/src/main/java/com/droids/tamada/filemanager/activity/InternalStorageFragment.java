package com.droids.tamada.filemanager.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droids.tamada.filemanager.adapter.InternalStorageFilesAdapter;
import com.droids.tamada.filemanager.helper.Utilities;
import com.droids.tamada.filemanager.model.InternalStorageFilesModel;
import com.example.satish.filemanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Satish on 04-12-2015.
 */
public class InternalStorageFragment extends Fragment implements InternalStorageFilesAdapter.CustomListener {
    private MediaPlayer mediaPlayer;
    private TextView startTime;
    private TextView endTime;
    private SeekBar seekBar;
    private ListView listView;
    private ArrayList<InternalStorageFilesModel> filesModelArrayList;
    private InternalStorageFilesAdapter internalStorageFilesAdapter;
    private boolean isChecked = false;
    private String menu_type = "mainMenu";
    private String root = "/sdcard";
    private String selectedFilePath;
    private String selectedFolderName;
    private int selectedFilePosition;
    private String fileExtension;
    private HashMap selectedFileHashMap = new HashMap();
    private Handler mHandler = new Handler();
    private Utilities utilities;
    private Toolbar toolbar;
    private ArrayList<String> listItemClickPaths;
    private String selectAllLabel = "selectAll";
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();
            // Displaying Total Duration time
            endTime.setText("" + utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            startTime.setText("" + utilities.milliSecondsToTimer(currentDuration));
            // Updating progress bar
            int progress = (int) (utilities.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    //generate conflicts
    public InternalStorageFragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Toast.makeText(getActivity().getApplicationContext(), "back pressed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu_type.equals("mainMenu"))
            inflater.inflate(R.menu.main_menu, menu);
        else {
            inflater.inflate(R.menu.menu_directory, menu);
            if (selectedFileHashMap.size() > 1) {
                menu.findItem(R.id.action_rename).setVisible(false);
                menu.findItem(R.id.action_select_all).setVisible(false);
            } else {
                menu.findItem(R.id.action_rename).setVisible(true);
                menu.findItem(R.id.action_select_all).setVisible(true);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_property:
                if (selectedFileHashMap.size() == 0)
                    getProperties();
                else
                    getFileProperty(selectedFilePath, selectedFolderName);
                break;
            case R.id.action_select_all:
                isChecked = true;
                changeCheckboxStatus();
                break;
            case R.id.action_de_select_all:
                isChecked = false;
                selectAllLabel = "deselectAll";
                getActivity().invalidateOptionsMenu();
                changeCheckboxStatus();
                break;
            case R.id.action_rename:
                renameFile();
                break;
            case R.id.action_copy_selection:
                break;
            case R.id.action_move_selection:
                break;
            case R.id.action_add_bookmarks:
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interanl, container, false);
        // btnDelete = (ImageButton) rootView.findViewById(R.id.btn_delete);
        listView = (ListView) rootView.findViewById(R.id.internal_file_list_view);
        listItemClickPaths = new ArrayList<>();
        listItemClickPaths.add(root);
        getDirectory(root);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbarbottom);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_folder:
                        createNewFolder();
                        break;
                    case R.id.action_new_file:
                        createNewFile(root);
                        break;
                    case R.id.action_delete:
                        deleteFile();
                        break;
                    case R.id.action_back_button:
                        navigateBackDir();
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_bottom);
        toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                InternalStorageFilesModel model = filesModelArrayList.get(position);
                if (!model.isSelected()) {
                    model.setSelected(true);//set true value for selected item
                    filesModelArrayList.remove(position);//remove the current selected item from list
                    filesModelArrayList.add(position, model);//add the updated item to list
                    internalStorageFilesAdapter.notifyDataSetChanged();//refresh the listview
                    //display the delete button
                    if (!selectedFileHashMap.containsKey(position))//if selected list item is not exits in selected hash map
                        //noinspection unchecked
                        selectedFileHashMap.put(model.getFileName(), model.getFilePath());//added the selected item to selected hash map
                    Toast.makeText(getActivity().getApplicationContext(), "" + selectedFileHashMap.size(), Toast.LENGTH_SHORT).show();
                    menu_type = "dirMenu";
                    getActivity().invalidateOptionsMenu();
                    toolbar.getMenu().findItem(R.id.action_delete).setVisible(true);//set menu tag as directory menu
                } else {
                    model.setSelected(false);
                    filesModelArrayList.remove(position);
                    filesModelArrayList.add(position, model);
                    internalStorageFilesAdapter.notifyDataSetChanged();
                    selectedFileHashMap.remove(model.getFileName());
                    if (selectedFileHashMap.size() == 0) {//if checked items list is empty then display the main menu,disable delete menu button
                        menu_type = "mainMenu";
                        getActivity().invalidateOptionsMenu();
                        toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);
                    } else {
                        menu_type = "dirMenu";//if checked items list not empty then display directory menu and enable delete menu button
                        getActivity().invalidateOptionsMenu();
                        toolbar.getMenu().findItem(R.id.action_delete).setVisible(true);
                    }
                }
                return true;
            }
        });
        //event on item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InternalStorageFilesModel model = filesModelArrayList.get(position);
                fileExtension = model.getFileName().substring(model.getFileName().lastIndexOf(".") + 1);
                File file = new File(model.getFilePath());//get the selected item path in list view
                // getDirectory(model.getFilePath());
                if (file.isDirectory()) {//check if selected item is directory
                    if (file.canRead()) {//if selected directory is readable
                        if (model.getFileName().equals("/"))//if filename root the we set dirctory path ../
                            getDirectory("/sdcard");
                        else
                            getDirectory(model.getFilePath());//if filename not root
                        listItemClickPaths.add(model.getFilePath());
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
                else if (fileExtension.equals("png") || fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {//if file type is image
                    Intent imageIntent = new Intent(getActivity().getApplicationContext(), ImageViewActivity.class);
                    imageIntent.putExtra("imagePath", model.getFilePath());
                    imageIntent.putExtra("imageName", model.getFileName());
                    getActivity().startActivity(imageIntent);
                } else if (fileExtension.equals("mp3")) {//if file type is audio
                    try {
                        getAudioPlayer(model.getFileName(), model.getFilePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

                } else if (fileExtension.equals("pdf")) {
                    getPdfReader(model.getFilePath());
                } else if (fileExtension.equals("mp4") || fileExtension.equals("3gp") || fileExtension.equals("wmv")) {
                    videoHandler(model.getFilePath());
                } else {
                }

            }//onItemClick
        });
        return rootView;
    }

    private void navigateBackDir() {
        if (listItemClickPaths.size() == 1)
            Toast.makeText(getActivity().getApplicationContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        if (listItemClickPaths.size() != 0) {
            if (listItemClickPaths.size() >= 2)
                getDirectory(listItemClickPaths.get(listItemClickPaths.size() - 2));
            listItemClickPaths.remove(listItemClickPaths.size() - 1);
        } else {
            getActivity().finish();
            System.exit(0);
        }
    }

    private void videoHandler(String filePath) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        Uri data = Uri.parse(filePath);
        intent.setDataAndType(data, "video/mp4");
        getActivity().startActivity(intent);
    }

    private void deleteFile() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Message
        alertDialog.setTitle("Delete Folder");
        alertDialog.setIcon(R.mipmap.ic_delete_folder);
        if (selectedFileHashMap.size() == 1)//if user select single folder
            alertDialog.setMessage(getActivity().getApplicationContext().getString(R.string.msg_prompt_delete_folder).replace("#name#", selectedFolderName.substring(0, selectedFolderName.length() - 1)));
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
                    for (int i = 0; i < selectedFileHashMap.size(); i++) {
                        File deleteFile = new File(selectedFilePath);//create file for selected file
                        boolean isDeleteFile = deleteFile.delete();//delete the file from memory
                        Log.d("delete file", selectedFilePath + "" + Boolean.toString(isDeleteFile));
                        if (isDeleteFile) {
                            InternalStorageFilesModel model = filesModelArrayList.get(selectedFilePosition);
                            filesModelArrayList.remove(model);//remove file from listview
                            internalStorageFilesAdapter.notifyDataSetChanged();//refresh the adapter
                            selectedFileHashMap.remove(selectedFolderName);
                            menu_type = "mainMenu";
                            getActivity().invalidateOptionsMenu();
                            toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);
                            //set menu tag for display main menu
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialog.show();
    }

    private void getPdfReader(String filePath) {
        File file = new File(filePath);
        PackageManager packageManager = getActivity().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0 && file.isFile()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            startActivity(intent);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "There is no app to handle this type of file", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUnZipDirectory(String zipFile, String outputFolder) {
        byte[] buffer = new byte[1024];
        try {
            File folder = new File(outputFolder);//create output directory is not exists
            if (!folder.exists()) {
                folder.mkdir();
            }
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));//get the zip file content
            ZipEntry ze = zis.getNextEntry(); //get the zipped file list entry
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

    private void getAudioPlayer(String fileName, String filePath) throws IOException {
        Dialog dialogMusicPlayer = new Dialog(getActivity());
        dialogMusicPlayer.setContentView(R.layout.custom_dialog_music_player);
        dialogMusicPlayer.setTitle(fileName);
        dialogMusicPlayer.show();
        seekBar = (SeekBar) dialogMusicPlayer.findViewById(R.id.volume_bar);
        startTime = (TextView) dialogMusicPlayer.findViewById(R.id.lbl_start_time);
        endTime = (TextView) dialogMusicPlayer.findViewById(R.id.lbl_end_time);
        final ImageButton btnPlayPause = (ImageButton) dialogMusicPlayer.findViewById(R.id.btnPlayPause);
        utilities = new Utilities();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
        updateProgressBar();
        dialogMusicPlayer.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mediaPlayer.stop();
            }
        });
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        // Changing button image to play button
                        btnPlayPause.setImageResource(R.mipmap.ic_play);
                    }
                } else {
                    // Resume song
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        // Changing button image to pause button
                        btnPlayPause.setImageResource(R.mipmap.ic_pause);
                    }
                }
            }
        });
        MediaPlayer mp = new MediaPlayer();
        mp.setDataSource(filePath);
        mp.prepare();
        mp.start();
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
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utilities.progressToTimer(seekBar.getProgress(), totalDuration);
                mediaPlayer.seekTo(currentPosition); // forward or backward to certain seconds
                updateProgressBar(); // update timer progress again
            }
        });
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
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

    private void createNewFile(final String rootPath) {
        final Dialog fileDialog = new Dialog(getActivity());
        fileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fileDialog.setContentView(R.layout.custom_new_file_dialog);//display custom file menu
        fileDialog.show();
        final EditText txtNewFile = (EditText) fileDialog.findViewById(R.id.txt_new_file);
        Button create = (Button) fileDialog.findViewById(R.id.btn_create);
        Button cancel = (Button) fileDialog.findViewById(R.id.btn_cancel);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = txtNewFile.getText().toString();
                if (fileName.equals("")) {//if user not enter text file name
                    fileName = "NewFile";
                } else {
                    fileName = txtNewFile.getText().toString();
                }
                try {
                    File file = new File(rootPath + "/" + fileName + ".txt");
                    if (file.exists()) {
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_file_already_exits), Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isCreated = file.createNewFile();
                        if (isCreated) {
                            InternalStorageFilesModel model = new InternalStorageFilesModel(fileName + ".txt", file.getPath(), false, false);
                            filesModelArrayList.add(model);
                            internalStorageFilesAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_file_created), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_file_not_created), Toast.LENGTH_SHORT).show();
                        }
                    }
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

    private void createNewFolder() {
        final Dialog fileDialog = new Dialog(getActivity());
        fileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fileDialog.setContentView(R.layout.custom_new_folder_dialog);//display custom file menu
        fileDialog.show();
        final EditText txtNewFolder = (EditText) fileDialog.findViewById(R.id.txt_new_folder);
        Button create = (Button) fileDialog.findViewById(R.id.btn_create);
        Button cancel = (Button) fileDialog.findViewById(R.id.btn_cancel);
        //create file event
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName = txtNewFolder.getText().toString();
                if (folderName.equals("")) {//if user not enter text file name
                    folderName = "NewFolder/";
                } else {
                    folderName = txtNewFolder.getText().toString() + "/";
                }
                try {
                    Log.d("root", root + "/" + folderName);
                    File file = new File(root + "/" + folderName);
                    if (file.exists()) {
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_folder_already_exits), Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isFolderCreated = file.mkdir();
                        if (isFolderCreated) {
                            InternalStorageFilesModel model = new InternalStorageFilesModel(folderName, root + "/" + folderName, false, true);
                            filesModelArrayList.add(model);
                            internalStorageFilesAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_folder_created), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_folder_not_created), Toast.LENGTH_SHORT).show();
                        }
                    }
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
        final Dialog propertyDialog = new Dialog(getActivity());
        propertyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        propertyDialog.setContentView(R.layout.custom_dialog_property);
        propertyDialog.show();
        TextView lblTotalDiskSize = (TextView) propertyDialog.findViewById(R.id.used_space);
        TextView lblFreeDiskSize = (TextView) propertyDialog.findViewById(R.id.free_space);
        Button btnCancel = (Button) propertyDialog.findViewById(R.id.btn_cancel);
        lblFreeDiskSize.setText(getAvailableInternalMemorySize());
        lblTotalDiskSize.setText(getTotalInternalMemorySize());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propertyDialog.cancel();
            }
        });
    }

    private void renameFile() {
        final Dialog renameDialog = new Dialog(getActivity());
        renameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                if (newFile.exists()) {//if renamed file already exits
                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_name_already_exits), Toast.LENGTH_LONG).show();
                    renameDialog.cancel();
                } else {
                    boolean isRenamed = oldFile.renameTo(newFile);
                    if (isRenamed) {
                        Toast.makeText(getActivity().getApplicationContext(), selectedFilePath.substring(0, selectedFilePath.lastIndexOf('/')) + renamed_file.getText().toString(), Toast.LENGTH_SHORT).show();
                        InternalStorageFilesModel model = filesModelArrayList.get(selectedFilePosition);
                        model.setFileName(renamed_file.getText().toString());
                        model.setFilePath(newFile.getPath());
                        if (newFile.isDirectory()) {
                            model.setIsDir(true);
                        } else {
                            model.setIsDir(false);
                        }
                        model.setSelected(false);
                        filesModelArrayList.remove(selectedFilePosition);
                        filesModelArrayList.add(selectedFilePosition, model);
                        internalStorageFilesAdapter.notifyDataSetChanged();
                        toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);
                        menu_type = "mainMenu";
                        getActivity().invalidateOptionsMenu();
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_renamed), Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_not_renamed), Toast.LENGTH_SHORT).show();
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
        Log.d("filePath", selectedFilePath);
        final Dialog propertyDialog = new Dialog(getActivity());
        propertyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        propertyDialog.setContentView(R.layout.custom_dialog_file_property);
        propertyDialog.show();
        TextView lbl_file_name = (TextView) propertyDialog.findViewById(R.id.selected_file_name);
        TextView lbl_file_size = (TextView) propertyDialog.findViewById(R.id.lbl_file_size);
        Button btnOk = (Button) propertyDialog.findViewById(R.id.btn_cancel);
        lbl_file_name.setText(selectedFileName.substring(0, selectedFileName.length() - 1));
        if (getTotalFileMemorySize(selectedFilePath).equals("0"))
            lbl_file_size.setText("0KB");
        else
            lbl_file_size.setText(getTotalFileMemorySize(selectedFilePath));//set l
        btnOk.setOnClickListener(new View.OnClickListener() {
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

    public void changeCheckboxStatus() {
        for (int i = 0; i < filesModelArrayList.size(); i++) {
            InternalStorageFilesModel fileModel = filesModelArrayList.get(i);//get the all filemodel elements
            if (!fileModel.getFileName().equals("/")) {
                fileModel.setSelected(isChecked);
            } else {
                fileModel.setSelected(false);
            }
            //set  is checked value by getting from the selected or deselected btn
            filesModelArrayList.set(i, fileModel);//replace the element on array list
            if (!isChecked) {
                selectedFileHashMap.remove(fileModel.getFileName());
            } else if (!selectAllLabel.contains(fileModel.getFileName())) {
                selectedFileHashMap.put(fileModel.getFileName(), fileModel.getFilePath());
            }
        }
        internalStorageFilesAdapter.notifyDataSetChanged();//set notify to list adapter

        if (isChecked) {
            menu_type = "dirMenu";
            getActivity().invalidateOptionsMenu();
            toolbar.getMenu().findItem(R.id.action_delete).setVisible(true);
        } else {
            menu_type = "mainMenu";//change menu type to main menu
            getActivity().invalidateOptionsMenu();
            toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);//disappear delete button
        }
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
        String selectedFileRootPath = root;
        root = model.getFilePath();//set the root to selected filepath
        selectedFilePath = model.getFilePath();
        selectedFolderName = model.getFileName();
        selectedFilePosition = position;
        model.setSelected(isChecked);
        filesModelArrayList.remove(position);
        filesModelArrayList.add(position, model);
        internalStorageFilesAdapter.notifyDataSetChanged();
        if (isChecked) {//add the item to selected hash map,display directory menu and enable delete menu button
            selectedFileHashMap.put(selectedFolderName, selectedFilePath);
            menu_type = "dirMenu";
            getActivity().invalidateOptionsMenu();
            toolbar.getMenu().findItem(R.id.action_delete).setVisible(true);
        } else {
            selectedFileHashMap.remove(selectedFolderName);
            if (selectedFileHashMap.size() == 0) {//if checked items list is empty then display the main menu,disable delete menu button
                menu_type = "mainMenu";
                getActivity().invalidateOptionsMenu();
                toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);
            } else {
                menu_type = "dirMenu";//if checked items list not empty then display directory menu and enable delete menu button
                getActivity().invalidateOptionsMenu();
                toolbar.getMenu().findItem(R.id.action_delete).setVisible(true);
            }
            root = selectedFileRootPath;
        }//end of else
    }
}