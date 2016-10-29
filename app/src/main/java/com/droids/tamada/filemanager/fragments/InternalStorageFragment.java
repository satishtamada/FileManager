package com.droids.tamada.filemanager.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.droids.tamada.filemanager.activity.ImageViewActivity;
import com.droids.tamada.filemanager.activity.MainActivity;
import com.droids.tamada.filemanager.activity.TextFileViewActivity;
import com.droids.tamada.filemanager.adapter.InternalStorageListAdapter;
import com.droids.tamada.filemanager.app.AppController;
import com.droids.tamada.filemanager.helper.PreferManager;
import com.droids.tamada.filemanager.helper.Utilities;
import com.droids.tamada.filemanager.model.InternalStorageFilesModel;
import com.example.satish.filemanager.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InternalStorageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InternalStorageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InternalStorageFragment extends Fragment implements MainActivity.ButtonBackPressListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private LinearLayout noMediaLayout;
    private OnFragmentInteractionListener mListener;
    private ArrayList<InternalStorageFilesModel> internalStorageFilesModelArrayList;
    private InternalStorageListAdapter internalStorageListAdapter;
    private String rootPath;
    private String fileExtension;
    private RelativeLayout footerAudioPlayer;
    private MediaPlayer mediaPlayer;
    private Utilities utilities;
    private RelativeLayout footerLayout;
    private TextView lblFilePath;
    private ArrayList<String> arrayListFilePaths;
    private ToggleButton toggleButtonCheck;
    private PreferManager preferManager;
    private String selectedFilePath;
    private String selectedFolderName;
    private int selectedFilePosition;

    public InternalStorageFragment() {
        // Required empty public constructor
    }

    public static InternalStorageFragment newInstance(String param1, String param2) {
        InternalStorageFragment fragment = new InternalStorageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_internal_storage, container, false);
        AppController.getInstance().setButtonBackPressed(this);
        preferManager = new PreferManager(AppController.getInstance().getApplicationContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noMediaLayout = (LinearLayout) view.findViewById(R.id.noMediaLayout);
        footerLayout = (RelativeLayout) view.findViewById(R.id.id_layout_footer);
        lblFilePath = (TextView) view.findViewById(R.id.id_file_path);
        ImageView imgDelete = (ImageView) view.findViewById(R.id.id_delete);
        ImageView imgFileCopy = (ImageView) view.findViewById(R.id.id_copy_file);
        ImageView imgMenu = (ImageView) view.findViewById(R.id.id_menu);
        internalStorageFilesModelArrayList = new ArrayList<>();
        arrayListFilePaths = new ArrayList<>();
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        internalStorageListAdapter = new InternalStorageListAdapter(internalStorageFilesModelArrayList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AppController.getInstance().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(internalStorageListAdapter);
        arrayListFilePaths.add(rootPath);
        getFilesList(rootPath);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(AppController.getInstance().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (footerLayout.getVisibility() != View.GONE) {
                    Animation topToBottom = AnimationUtils.loadAnimation(AppController.getInstance().getApplicationContext(),
                            R.anim.top_bottom);
                    footerLayout.startAnimation(topToBottom);
                    footerLayout.setVisibility(View.GONE);
                }
                InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(position);
                if (!internalStorageFilesModel.isSelected()) {//if file not selected then open file.
                    fileExtension = internalStorageFilesModel.getFileName().substring(internalStorageFilesModel.getFileName().lastIndexOf(".") + 1);//file extension (.mp3,.png,.pdf)
                    File file = new File(internalStorageFilesModel.getFilePath());//get the selected item path
                    if (file.isDirectory()) {//check if selected item is directory
                        if (file.canRead()) {//if directory is readable
                            internalStorageFilesModelArrayList.clear();
                            arrayListFilePaths.add(internalStorageFilesModel.getFilePath());
                            getFilesList(internalStorageFilesModel.getFilePath());
                            internalStorageListAdapter.notifyDataSetChanged();
                        } else {//Toast to your not openable type
                            Toast.makeText(AppController.getInstance().getApplicationContext(), "Folder can't be read!", Toast.LENGTH_SHORT).show();
                        }
                        //if file is not directory open a application for file type
                    } else if (fileExtension.equals("png") || fileExtension.equals("jpeg") || fileExtension.equals("jpg")) {
                        Intent imageIntent = new Intent(getActivity().getApplicationContext(), ImageViewActivity.class);
                        imageIntent.putExtra("imagePath", internalStorageFilesModel.getFilePath());
                        imageIntent.putExtra("imageName", internalStorageFilesModel.getFileName());
                        getActivity().startActivity(imageIntent);
                    } else if (fileExtension.equals("mp3")) {
                        showAudioPlayer(internalStorageFilesModel.getFileName(), internalStorageFilesModel.getFilePath());
                    } else if (fileExtension.equals("txt") || fileExtension.equals("html") || fileExtension.equals("xml")) {
                        Intent txtIntent = new Intent(getActivity().getApplicationContext(), TextFileViewActivity.class);
                        txtIntent.putExtra("filePath", internalStorageFilesModel.getFilePath());
                        txtIntent.putExtra("fileName", internalStorageFilesModel.getFileName());
                        getActivity().startActivity(txtIntent);
                    } else if (fileExtension.equals("zip") || fileExtension.equals("rar")) {
                        //TODO handle zip file
                    } else if (fileExtension.equals("pdf")) {
                        File pdfFile = new File(internalStorageFilesModel.getFilePath());
                        PackageManager packageManager = getActivity().getPackageManager();
                        Intent testIntent = new Intent(Intent.ACTION_VIEW);
                        testIntent.setType("application/pdf");
                        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (list.size() > 0 && pdfFile.isFile()) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri uri = Uri.fromFile(pdfFile);
                            intent.setDataAndType(uri, "application/pdf");
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "There is no app to handle this type of file", Toast.LENGTH_SHORT).show();
                        }
                    } else if (fileExtension.equals("mp4") || fileExtension.equals("3gp") || fileExtension.equals("wmv")) {
                        Uri fileUri = Uri.fromFile(new File(internalStorageFilesModel.getFileName()));
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(fileUri, "video/mp4");
                        getActivity().startActivity(intent);
                    }
                } else {
                  /*  toggleButtonCheck = (ToggleButton) view.findViewById(R.id.id_check);
                    toggleButtonCheck.setChecked(false);
                    internalStorageFilesModel.setSelected(false);*/
                }
            }

            @Override
            public void onLongClick(View view, int position) {
               /* if (footerLayout.getVisibility() != View.VISIBLE) {
                    Animation bottomToTop = AnimationUtils.loadAnimation(AppController.getInstance().getApplicationContext(),
                            R.anim.bottom_top);
                    footerLayout.startAnimation(bottomToTop);
                    footerLayout.setVisibility(View.VISIBLE);
                }
                toggleButtonCheck = (ToggleButton) view.findViewById(R.id.id_check);
                toggleButtonCheck.setChecked(true);
                InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(position);
                internalStorageFilesModel.setSelected(true);
                selectedFilePath = internalStorageFilesModel.getFilePath();
                selectedFolderName = internalStorageFilesModel.getFileName();
                selectedFilePosition = position;*/

            }

        }));

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO delete file,folder

            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                footerLayout.setVisibility(View.GONE);
                showMenu();
            }
        });

        imgFileCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO new file
                Toast.makeText(getActivity().getApplicationContext(), "copy file", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    private void getFilesList(String filePath) {
        lblFilePath.setText(filePath);
        Log.d("length", "" + arrayListFilePaths.size());
        File f = new File(filePath);
        File[] files = f.listFiles();
        if (files.length == 0) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMediaLayout.setVisibility(View.GONE);
        }
        for (File file : files) {
            InternalStorageFilesModel model = new InternalStorageFilesModel();
            model.setFileName(file.getName());
            model.setFilePath(file.getPath());
            model.setCheckboxVisible(false);
            model.setSelected(false);
            if (file.isDirectory()) {
                model.setDir(true);
            } else {
                model.setDir(false);
            }
            internalStorageFilesModelArrayList.add(model);
        }
    }

    @Override
    public void onButtonBackPressed(int navItemIndex) {
        if (footerLayout.getVisibility() != View.GONE) {
            Animation topToBottom = AnimationUtils.loadAnimation(AppController.getInstance().getApplicationContext(),
                    R.anim.top_bottom);
            footerLayout.startAnimation(topToBottom);
            footerLayout.setVisibility(View.GONE);
        }
        if (navItemIndex == 0) {
            if (arrayListFilePaths.size() == 1) {
                Toast.makeText(AppController.getInstance().getApplicationContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            }
            if (arrayListFilePaths.size() != 0) {
                if (arrayListFilePaths.size() >= 2) {
                    internalStorageFilesModelArrayList.clear();
                    getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 2));
                    internalStorageListAdapter.notifyDataSetChanged();
                }
                arrayListFilePaths.remove(arrayListFilePaths.size() - 1);
            } else {
                getActivity().finish();
                System.exit(0);
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void createNewFile() {
        final Dialog dialogNewFile = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialogNewFile.setContentView(R.layout.custom_new_file_dialog);
        dialogNewFile.show();
        final EditText txtNewFile = (EditText) dialogNewFile.findViewById(R.id.txt_new_folder);
        Button btnCreate = (Button) dialogNewFile.findViewById(R.id.btn_create);
        Button btnCancel = (Button) dialogNewFile.findViewById(R.id.btn_cancel);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = txtNewFile.getText().toString().trim();
                if (fileName.length() == 0) {//if file name is empty
                    fileName = "NewFile";
                }
                try {
                    File file = new File(rootPath + "/" + fileName + ".txt");
                    if (file.exists()) {
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_file_already_exits), Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isCreated = file.createNewFile();
                        if (isCreated) {
                            InternalStorageFilesModel model = new InternalStorageFilesModel(fileName + ".txt", file.getPath(), false, false, false);
                            internalStorageFilesModelArrayList.add(model);
                            internalStorageListAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_file_created), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_file_not_created), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialogNewFile.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNewFile.setText("");
                dialogNewFile.dismiss();
            }
        });
    }

    public void createNewFolder() {
        final Dialog dialogNewFolder = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialogNewFolder.setContentView(R.layout.custom_new_folder_dialog);
        dialogNewFolder.show();
        final EditText txtNewFolder = (EditText) dialogNewFolder.findViewById(R.id.txt_new_folder);
        Button btnCreate = (Button) dialogNewFolder.findViewById(R.id.btn_create);
        Button btnCancel = (Button) dialogNewFolder.findViewById(R.id.btn_cancel);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName = txtNewFolder.getText().toString().trim();
                if (folderName.length() == 0) {//if user not enter text file name
                    folderName = "NewFolder";
                }
                try {
                    File file = new File(rootPath + "/" + folderName);
                    if (file.exists()) {
                        Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_folder_already_exits), Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isFolderCreated = file.mkdir();
                        if (isFolderCreated) {
                            InternalStorageFilesModel model = new InternalStorageFilesModel(folderName, rootPath + "/" + folderName, true, false, false);
                            internalStorageFilesModelArrayList.add(model);
                            internalStorageListAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_folder_created), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getString(R.string.msg_prompt_folder_not_created), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialogNewFolder.cancel();
            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNewFolder.setText("");
                dialogNewFolder.dismiss();
            }
        });

    }

    public void searchFile() {
        //TODO search file
        Toast.makeText(AppController.getInstance().getApplicationContext(), "hello search file", Toast.LENGTH_SHORT).show();

    }

    private void showMenu() {
        final Dialog menuDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        menuDialog.setContentView(R.layout.custom_menu_dialog);
        TextView lblRenameFile = (TextView) menuDialog.findViewById(R.id.id_rename);
        TextView lblFileDetails = (TextView) menuDialog.findViewById(R.id.id_file_details);
        lblRenameFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
                renameFile();
            }
        });
        lblFileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
                showFileDetails();
            }
        });
        menuDialog.show();
    }

    private void renameFile() {
        final Dialog dialogRenameFile = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialogRenameFile.setContentView(R.layout.custom_rename_file_dialog);
        dialogRenameFile.show();
        final EditText txtRenameFile = (EditText) dialogRenameFile.findViewById(R.id.txt_file_name);
        Button btnRename = (Button) dialogRenameFile.findViewById(R.id.btn_rename);
        Button btnCancel = (Button) dialogRenameFile.findViewById(R.id.btn_cancel);
        txtRenameFile.setText(selectedFolderName);
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtRenameFile.getText().toString().trim().length() == 0) {
                    Toast.makeText(AppController.getInstance().getApplicationContext(), "Please enter file name", Toast.LENGTH_SHORT).show();
                } else {
                    File renamedFile = new File(selectedFilePath.substring(0, selectedFilePath.lastIndexOf('/') + 1) + txtRenameFile.getText().toString());
                    if (renamedFile.exists()) {
                        Toast.makeText(AppController.getInstance().getApplicationContext(), "File already exits,choose another name", Toast.LENGTH_SHORT).show();
                    } else {
                        final File oldFile = new File(selectedFilePath);//create file with old name
                        boolean isRenamed = oldFile.renameTo(renamedFile);
                        if (isRenamed) {
                            InternalStorageFilesModel model = internalStorageFilesModelArrayList.get(selectedFilePosition);
                            model.setFileName(txtRenameFile.getText().toString());
                            model.setFilePath(renamedFile.getPath());
                            if (renamedFile.isDirectory()) {
                                model.setIsDir(true);
                            } else {
                                model.setIsDir(false);
                            }
                            model.setSelected(false);
                            internalStorageFilesModelArrayList.remove(selectedFilePosition);
                            internalStorageFilesModelArrayList.add(selectedFilePosition, model);
                            internalStorageListAdapter.notifyDataSetChanged();
                            dialogRenameFile.dismiss();
                        } else {
                            Toast.makeText(AppController.getInstance().getApplicationContext(), AppController.getInstance().getApplicationContext().getString(R.string.msg_prompt_not_renamed), Toast.LENGTH_SHORT).show();
                            dialogRenameFile.dismiss();
                        }
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRenameFile.setText("");
                dialogRenameFile.dismiss();
            }
        });
    }

    private void showFileDetails() {
        final Dialog menuDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        menuDialog.setContentView(R.layout.custom_menu_dialog);
    }


    private void showAudioPlayer(String fileName, String filePath) {
        final Dialog audioPlayerDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        audioPlayerDialog.setContentView(R.layout.custom_audio_player_dialog);
        footerAudioPlayer = (RelativeLayout) audioPlayerDialog.findViewById(R.id.id_layout_audio_player);
        TextView lblAudioFileName = (TextView) audioPlayerDialog.findViewById(R.id.ic_audio_file_name);
        ToggleButton toggleBtnPlayPause = (ToggleButton) audioPlayerDialog.findViewById(R.id.id_play_pause);
        toggleBtnPlayPause.setChecked(true);
        lblAudioFileName.setText(fileName);
        audioPlayerDialog.show();
        mediaPlayer = new MediaPlayer();
        utilities = new Utilities();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        footerAudioPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                audioPlayerDialog.dismiss();
            }
        });
        toggleBtnPlayPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                }
            }
        });
        audioPlayerDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    audioPlayerDialog.dismiss();
                }
                return true;
            }
        });
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
