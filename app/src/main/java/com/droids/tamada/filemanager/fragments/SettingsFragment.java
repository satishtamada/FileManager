package com.droids.tamada.filemanager.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.droids.tamada.filemanager.activity.AboutActivity;
import com.droids.tamada.filemanager.app.AppController;
import com.droids.tamada.filemanager.helper.PreferManager;
import com.droids.tamada.filemanager.helper.SwitchButton;
import com.example.satish.filemanager.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SwitchButton sBtnLock, sBtnShowHiddenFile;
    private Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine, btnZero, btnCancel;
    private ImageView imgDelete;
    private String tempPassword = "";
    private TextView lblEnterPassword;
    private String password = "";
    private String rePassword;
    private EditText txtPassword;
    private Dialog appLockDialog;
    private ArrayList<String> pswArray;
    private int passwordLength;
    private PreferManager preferManager;
    private TextView lblAbout;
    private InterstitialAd mInterstitialAd;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sBtnLock = (SwitchButton) view.findViewById(R.id.id_setting_lock);
        sBtnShowHiddenFile = (SwitchButton) view.findViewById(R.id.id_setting_hide_file);
        lblAbout= (TextView) view.findViewById(R.id.id_about);
        pswArray = new ArrayList<>();
        mInterstitialAd = new InterstitialAd(AppController.getInstance().getApplicationContext());

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
        preferManager = new PreferManager(AppController.getInstance().getApplicationContext());
        if (preferManager.isPasswordActivated()) {
            sBtnLock.setChecked(true);
        } else {
            sBtnLock.setChecked(false);
        }
        if (preferManager.isHiddenFileVisible()) {
            sBtnShowHiddenFile.setChecked(true);
        } else {
            sBtnShowHiddenFile.setChecked(false);
        }
        sBtnLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (preferManager.getPassword().length() == 0) {
                        showPasswordDialog();
                    } else {
                        preferManager.setPasswordActivated(true);
                    }
                } else {
                    preferManager.setPasswordActivated(false);
                }
            }
        });
        sBtnShowHiddenFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    preferManager.setHiddenFileVisible(true);
                } else {
                    preferManager.setHiddenFileVisible(false);
                }
            }
        });
        lblAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AppController.getInstance().getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void showPasswordDialog() {
        appLockDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        appLockDialog.setContentView(R.layout.custom_app_lock_dialog);
        btnOne = (Button) appLockDialog.findViewById(R.id.id_one);
        btnTwo = (Button) appLockDialog.findViewById(R.id.id_two);
        btnThree = (Button) appLockDialog.findViewById(R.id.id_three);
        btnFour = (Button) appLockDialog.findViewById(R.id.id_four);
        btnFive = (Button) appLockDialog.findViewById(R.id.id_five);
        btnSix = (Button) appLockDialog.findViewById(R.id.id_six);
        btnSeven = (Button) appLockDialog.findViewById(R.id.id_seven);
        btnEight = (Button) appLockDialog.findViewById(R.id.id_eight);
        btnNine = (Button) appLockDialog.findViewById(R.id.id_nine);
        btnZero = (Button) appLockDialog.findViewById(R.id.id_zero);
        btnCancel = (Button) appLockDialog.findViewById(R.id.id_cancel);
        imgDelete = (ImageView) appLockDialog.findViewById(R.id.id_delete);
        txtPassword = (EditText) appLockDialog.findViewById(R.id.id_password);
        lblEnterPassword = (TextView) appLockDialog.findViewById(R.id.id_lbl_password);
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("1");
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("2");
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("3");
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("4");
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("5");
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("6");
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("7");
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("8");
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("9");
            }
        });
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("0");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appLockDialog.dismiss();
                sBtnLock.setChecked(false);
            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePassword();
            }
        });
        appLockDialog.show();
    }

    private void removePassword() {
        passwordLength = pswArray.size();
        Log.d("psw length", "" + passwordLength);
        if (passwordLength > 0) {
            pswArray.remove(passwordLength - 1);
            tempPassword = tempPassword.substring(0, passwordLength - 1);
            txtPassword.setText(tempPassword);
            Log.d("remove psw", tempPassword);
        }
    }

    private void setPassword(String strPassword) {
        passwordLength = pswArray.size();
        if (passwordLength < 4) {
            pswArray.add(passwordLength, strPassword);
            tempPassword = tempPassword + pswArray.get(passwordLength);
            txtPassword.setText(tempPassword);
            Log.d("password", tempPassword);
        }
        if (passwordLength == 3) {
            if (password.length() == 0) {
                lblEnterPassword.setText("Re-enter password");
                password = tempPassword;
                txtPassword.setText("");
                tempPassword = "";
                passwordLength = 0;
                pswArray.clear();
            } else {
                rePassword = tempPassword;
                if (password.equals(rePassword)) {
                    Toast.makeText(AppController.getInstance().getApplicationContext(), "corrcet", Toast.LENGTH_SHORT).show();
                    preferManager.setPassword(password);
                    preferManager.setPasswordActivated(true);
                    appLockDialog.dismiss();
                } else {
                    Toast.makeText(AppController.getInstance().getApplicationContext(), "miss match", Toast.LENGTH_SHORT).show();
                    rePassword = "";
                    tempPassword = "";
                    txtPassword.setText("");
                    passwordLength = 0;
                    pswArray.clear();
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
