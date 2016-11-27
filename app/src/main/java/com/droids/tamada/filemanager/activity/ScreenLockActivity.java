package com.droids.tamada.filemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.droids.tamada.filemanager.app.AppController;
import com.droids.tamada.filemanager.helper.PreferManager;
import com.example.satish.filemanager.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

/**
 * Created by inventbird on 19/10/16.
 */
public class ScreenLockActivity extends AppCompatActivity {
    private PreferManager preferManager;
    private Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine, btnZero, btnCancel;
    private ImageView imgDelete;
    private String tempPassword = "";
    private EditText txtPassword;
    private ArrayList<String> pswArray;
    private int passwordLength;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferManager = new PreferManager(AppController.getInstance().getApplicationContext());
        if (!preferManager.isPasswordActivated()) {
            Intent intent = new Intent(AppController.getInstance().getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_screen_lock);
            btnOne = (Button) findViewById(R.id.id_one);
            btnTwo = (Button) findViewById(R.id.id_two);
            btnThree = (Button) findViewById(R.id.id_three);
            btnFour = (Button) findViewById(R.id.id_four);
            btnFive = (Button) findViewById(R.id.id_five);
            btnSix = (Button) findViewById(R.id.id_six);
            btnSeven = (Button) findViewById(R.id.id_seven);
            btnEight = (Button) findViewById(R.id.id_eight);
            btnNine = (Button) findViewById(R.id.id_nine);
            btnZero = (Button) findViewById(R.id.id_zero);
            btnCancel = (Button) findViewById(R.id.id_cancel);
            imgDelete = (ImageView) findViewById(R.id.id_delete);
            txtPassword = (EditText) findViewById(R.id.id_password);
            // set the ad unit ID
            mInterstitialAd = new InterstitialAd(AppController.getInstance().getApplicationContext());
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
            pswArray = new ArrayList<>();
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
                    finish();
                }
            });
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removePassword();
                }
            });
        }
    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
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
            if (tempPassword.equals(preferManager.getPassword())) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AppController.getInstance().getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                tempPassword = "";
                txtPassword.setText("");
                passwordLength = 0;
                pswArray.clear();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getInstance().trackScreenView("ScreenLock screen");
    }
}
