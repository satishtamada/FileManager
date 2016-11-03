package com.droids.tamada.filemanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
@SuppressWarnings("ALL")
public class TextFileViewActivity extends AppCompatActivity {
    private String filePath;
    private EditText txtTextData;
    private String currentText, newText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview);
        txtTextData = (EditText) findViewById(R.id.txt_file_data);
        Intent txtIntent = getIntent();
        String fileName = txtIntent.getStringExtra("fileName");
        filePath = txtIntent.getStringExtra("filePath");
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(fileName);
        }
        try {
            txtTextData.setText(readTxt(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        currentText = txtTextData.getText().toString();//get current text from text file
        newText = currentText;//set new text as current text
        txtTextData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                newText = s.toString();//if text changed set new text as edittext text
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_save:
                saveText();//save the text file
                currentText = txtTextData.getText().toString();//set the current text as saved text
                return true;
            case R.id.action_text_size:
                return true;
            case R.id.action_text_color:
                return true;
            case R.id.action_background_color:
                return true;
            //change font size
            case R.id.ten:
                txtTextData.setTextSize(10);
                return true;
            case R.id.twelve:
                txtTextData.setTextSize(12);
                return true;
            case R.id.fourteen:
                txtTextData.setTextSize(14);
                return true;
            case R.id.sixteen:
                txtTextData.setTextSize(16);
                return true;
            case R.id.eighteen:
                txtTextData.setTextSize(18);
                return true;
            case R.id.twenty:
                txtTextData.setTextSize(20);
                return true;
            case R.id.twenty_two:
                txtTextData.setTextSize(22);
                return true;
            case R.id.twenty_four:
                txtTextData.setTextSize(24);
                return true;
            case R.id.twenty_six:
                txtTextData.setTextSize(26);
                return true;
            case R.id.twenty_eight:
                txtTextData.setTextSize(28);
                return true;
            case R.id.thirty:
                txtTextData.setTextSize(30);
                return true;
            case R.id.action_text_black://if text black color
                txtTextData.setTextColor(getResources().getColor(R.color.txt_black));
                txtTextData.setBackgroundColor(getResources().getColor(R.color.txt_white));
                break;
            case R.id.action_bg_white://background white
                txtTextData.setBackgroundColor(getResources().getColor(R.color.txt_white));
                txtTextData.setTextColor(getResources().getColor(R.color.txt_black));
                return true;
            case R.id.action_text_white://if text white color
                txtTextData.setTextColor(getResources().getColor(R.color.txt_white));
                txtTextData.setBackgroundColor(getResources().getColor(R.color.txt_black));
                return true;
            case R.id.action_bg_black://background color black
                txtTextData.setBackgroundColor(getResources().getColor(R.color.txt_black));
                txtTextData.setTextColor(getResources().getColor(R.color.txt_white));
                return true;
            case R.id.action_text_gray:
                txtTextData.setTextColor(getResources().getColor(R.color.txt_gray));
                return true;
            case R.id.action_text_green:
                txtTextData.setTextColor(getResources().getColor(R.color.txt_green));
                return true;
            case R.id.action_text_blue:
                txtTextData.setTextColor(getResources().getColor(R.color.txt_blue));
                return true;
            //set background color
            case R.id.action_bg_gray:
                txtTextData.setBackgroundColor(getResources().getColor(R.color.txt_gray));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!currentText.equals(newText))//if old text is not equal to newly updated text
            showSaveDialog();//give a dialog for save text
        else
            super.onBackPressed();//if old text is equal to new text close the activity without save
    }

    private void showSaveDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
        // Setting Dialog Message
        alertDialog.setTitle("Save file");
        alertDialog.setIcon(R.mipmap.ic_dialog_save);
        alertDialog.setMessage(getApplicationContext().getString(R.string.msg_prompt_save_text_file));
        alertDialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                txtTextData.setText(currentText);//set text value to old text
                saveText();
                finish();//close the activity
            }
        });
        //display confirm dialog for delete file or folder
        alertDialog.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveText();
                finish();
            }
        });
        alertDialog.show();
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
            //TODO Auto-generated catch block
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }
}
