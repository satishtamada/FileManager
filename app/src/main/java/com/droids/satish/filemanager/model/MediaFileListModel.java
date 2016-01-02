package com.droids.satish.filemanager.model;

/**
 * Created by Satish on 29-12-2015.
 */
public class MediaFileListModel {
    private String file_name, file_path;

    public MediaFileListModel() {
    }

    public MediaFileListModel(String audio_name) {
        this.file_name = audio_name;
    }

    public MediaFileListModel(String audio_name, String audio_file_path) {
        this.file_name = audio_name;
        this.file_path = audio_file_path;
    }

    public String getFileName() {
        return file_name;
    }

    public void setFileName(String audio_name) {
        this.file_name = audio_name;
    }

    public String getFilePath() {
        return file_path;
    }

    public void setFilePath(String audio_file_path) {
        this.file_path = audio_file_path;
    }
}