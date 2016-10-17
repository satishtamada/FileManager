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

import com.example.satish.filemanager.R;

import java.io.File;

/**
 * Created by inventbird on 17/10/16.
 */
public class ImageViewActivity extends AppCompatActivity {
    private ImageView imageView,imgBackArrow;
    private TextView lblImageName;
    String imageName, imagePath,imagePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        imageView = (ImageView) findViewById(R.id.imageView);
        lblImageName= (TextView) findViewById(R.id.idImageName);
        imgBackArrow= (ImageView) findViewById(R.id.id_back_arrow);
        Intent intent = getIntent();
        imageName = intent.getStringExtra("imageName");
        imagePath = intent.getStringExtra("imagePath");
        imagePosition=intent.getStringExtra("imagePosition");
        lblImageName.setText(imagePosition);
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
