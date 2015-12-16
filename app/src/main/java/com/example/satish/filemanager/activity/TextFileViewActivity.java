package com.example.satish.filemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.example.satish.filemanager.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Satish on 16-12-2015.
 */
public class TextFileViewActivity extends AppCompatActivity {
    private String fileName, filePath;
    private TextView lblTextName;
    private EditText txtTextData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview);
        lblTextName = (TextView) findViewById(R.id.lbl_text_file_name);
        txtTextData = (EditText) findViewById(R.id.txt_file_data);
        Intent txtIntent = getIntent();
        fileName = txtIntent.getStringExtra("fileName");
        filePath = txtIntent.getStringExtra("filePath");
        lblTextName.setText(fileName);
        try {
            txtTextData.setText(readTxt(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private String readTxt(String filePath) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(filePath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }
}
