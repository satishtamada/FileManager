package com.droids.tamada.filemanager.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noMediaLayout = (LinearLayout) view.findViewById(R.id.noMediaLayout);
        footerLayout = (RelativeLayout) view.findViewById(R.id.id_layout_footer);
        lblFilePath = (TextView) view.findViewById(R.id.id_file_path);
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
                    toggleButtonCheck = (ToggleButton) view.findViewById(R.id.id_check);
                    toggleButtonCheck.setChecked(false);
                    internalStorageFilesModel.setSelected(false);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (footerLayout.getVisibility() != View.VISIBLE) {
                    Animation bottomToTop = AnimationUtils.loadAnimation(AppController.getInstance().getApplicationContext(),
                            R.anim.bottom_top);
                    footerLayout.startAnimation(bottomToTop);
                    footerLayout.setVisibility(View.VISIBLE);
                }
                toggleButtonCheck = (ToggleButton) view.findViewById(R.id.id_check);
                toggleButtonCheck.setChecked(true);
                InternalStorageFilesModel internalStorageFilesModel = internalStorageFilesModelArrayList.get(position);
                internalStorageFilesModel.setSelected(true);
            }
        }));
        return view;
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
            if (file.getName().indexOf('.') != 0) {//	reveal folders only
                InternalStorageFilesModel model = new InternalStorageFilesModel(file.getName(), file.getPath(), false);
                internalStorageFilesModelArrayList.add(model);
            }
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
                Toast.makeText(AppController.getInstance().getApplicationContext(), "back pre", Toast.LENGTH_SHORT).show();
            } else {
                if (arrayListFilePaths.size() >= 2) {
                    internalStorageFilesModelArrayList.clear();
                    getFilesList(arrayListFilePaths.get(arrayListFilePaths.size() - 2));
                    internalStorageListAdapter.notifyDataSetChanged();
                    arrayListFilePaths.remove(arrayListFilePaths.size() - 1);
                }
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
