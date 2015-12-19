package com.example.satish.filemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.satish.filemanager.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Satish on 16-12-2015.
 */
public class TextFileViewActivity extends AppCompatActivity {
    private String fileName, filePath;
    private EditText txtTextData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview);
        txtTextData = (EditText) findViewById(R.id.txt_file_data);
        Intent txtIntent = getIntent();
        fileName = txtIntent.getStringExtra("fileName");
        filePath = txtIntent.getStringExtra("filePath");
        try {
            txtTextData.setText(readTxt(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
      /*  btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.action_search) {
            saveText();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveText() {
        String data = txtTextData.getText().toString();
        File file = new File(filePath);
        if (file.exists()) {
            try {
                File newTextFile = new File(filePath);
                FileWriter fw = new FileWriter(newTextFile);
                fw.write(data);
                fw.close();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException iox) {
                //do stuff with exception
                iox.printStackTrace();
            }
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
