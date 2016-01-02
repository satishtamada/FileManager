package com.droids.satish.filemanager.model;

/**
 * Created by Satish on 19-12-2015.
 */
public class TextEditorOptionsModel {
    int textSize;

    public TextEditorOptionsModel(int textSize) {
        this.textSize = textSize;
    }

    public TextEditorOptionsModel() {
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
