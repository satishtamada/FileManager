package com.example.satish.filemanager.model;

/**
 * Created by Satish on 29-12-2015.
 */
public class AudioListModel {
    private String audio_name,audio_file_path;

    public AudioListModel() {
    }

    public AudioListModel(String audio_name) {
        this.audio_name = audio_name;
    }

    public String getAudio_name() {
        return audio_name;
    }

    public void setAudio_name(String audio_name) {
        this.audio_name = audio_name;
    }

    public String getAudio_file_path() {
        return audio_file_path;
    }

    public AudioListModel(String audio_name, String audio_file_path) {
        this.audio_name = audio_name;
        this.audio_file_path = audio_file_path;
    }

    public void setAudio_file_path(String audio_file_path) {
        this.audio_file_path = audio_file_path;
    }
}
