package com.droids.tamada.filemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.droids.tamada.filemanager.app.AppController;
import com.droids.tamada.filemanager.helper.PreferManager;
import com.example.satish.filemanager.R;

/**
 * Created by inventbird on 19/10/16.
 */
public class ScreenLockActivity extends AppCompatActivity {
    private PreferManager preferManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferManager=new PreferManager(AppController.getInstance().getApplicationContext());
        if(!preferManager.isPasswordActivated()){
            Intent intent=new Intent(AppController.getInstance().getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_screen_lock);

        }
    }
}
