package com.droids.tamada.filemanager.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.droids.tamada.filemanager.helper.TouchImageView;
import com.example.satish.filemanager.R;

import java.io.File;

/**
 * Created by satish on 12/11/16.
 */

public class FullImageViewActivity extends AppCompatActivity {
    private ImageView imgBackArrow;
    private TouchImageView imageView;
    String  imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_imageview);
        imageView = (TouchImageView) findViewById(R.id.imageView);
        imgBackArrow= (ImageView) findViewById(R.id.id_back_arrow);
        Intent intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");
        imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
    }
}
