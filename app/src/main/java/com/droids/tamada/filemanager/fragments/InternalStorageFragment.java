package com.droids.tamada.filemanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.droids.tamada.filemanager.adapter.InternalStorageListAdapter;
import com.droids.tamada.filemanager.app.AppController;
import com.droids.tamada.filemanager.helper.DividerItemDecoration;
import com.droids.tamada.filemanager.model.InternalStorageFilesModel;
import com.example.satish.filemanager.R;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InternalStorageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InternalStorageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InternalStorageFragment extends Fragment {
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noMediaLayout = (LinearLayout) view.findViewById(R.id.noMediaLayout);
        internalStorageFilesModelArrayList = new ArrayList<>();
        rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        internalStorageListAdapter = new InternalStorageListAdapter(internalStorageFilesModelArrayList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AppController.getInstance().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(AppController.getInstance().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(internalStorageListAdapter);
        getFilesList(rootPath);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(AppController.getInstance().getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                InternalStorageFilesModel internalStorageFilesModel= internalStorageFilesModelArrayList.get(position);
                fileExtension = internalStorageFilesModel.getFileName().substring(internalStorageFilesModel.getFileName().lastIndexOf(".") + 1);//file extension (.mp3,.png,.pdf)
                File file = new File(internalStorageFilesModel.getFilePath());//get the selected item path
                if (file.isDirectory()) {//check if selected item is directory
                    if (file.canRead()) {//if directory is readable
                        getFilesList(rootPath);
                    } else {//Toast to your not openable type
                        Toast.makeText(AppController.getInstance().getApplicationContext(),"Folder can't be read!",Toast.LENGTH_SHORT).show();
                    }
                }else{//if file is not directory open a application for file type
                    //TODO open a application for it type
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return view;
    }

    private void getFilesList(String rootPath) {
        File f = new File(rootPath);
        File[] files = f.listFiles();
        if (files.length == 0) {
            noMediaLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noMediaLayout.setVisibility(View.GONE);
        }
        for (File file : files) {
            InternalStorageFilesModel model = new InternalStorageFilesModel(file.getName(), file.getPath(), false);
            internalStorageFilesModelArrayList.add(model);
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
