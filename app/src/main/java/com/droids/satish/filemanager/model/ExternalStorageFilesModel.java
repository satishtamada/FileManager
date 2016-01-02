package com.droids.satish.filemanager.model;

/**
 * Created by Satish on 26-12-2015.
 */
public class ExternalStorageFilesModel {
    private String fileName;
    private String filePath;
    private boolean selected;
    private boolean isDir;

    public ExternalStorageFilesModel() {
    }

    public ExternalStorageFilesModel(String fileName, String filePath, boolean selected, boolean isDir) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.selected = selected;
        this.isDir = isDir;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setIsDir(boolean isDir) {
        this.isDir = isDir;
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

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
