package com.droids.tamada.filemanager.model;

/**
 * Created by Satish on 29-12-2015.
 */
public class MediaFileListModel {
    private String fileName, filePath,fileSize,fileCreatedTime;

    public MediaFileListModel() {
    }

    public String getFileCreatedTime() {
        return fileCreatedTime;
    }

    public void setFileCreatedTime(String fileCreatedTime) {
        this.fileCreatedTime = fileCreatedTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}