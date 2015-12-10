package com.example.satish.filemanager.model;

/**
 * Created by Satish on 05-12-2015.
 */
public class InternalStorageFilesModel {
    private String fileName;
    private String filePath;
    private boolean selected;

    public InternalStorageFilesModel() {
    }

    public InternalStorageFilesModel(String fileName, String filePath ,boolean selected) {
        this.filePath=filePath;
        this.fileName = fileName;
        this.selected = selected;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
