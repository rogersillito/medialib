package com.rogersillito.medialib.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class AudioFile extends MediaFile {
    @Getter
    @Setter
    private String artist;
    @Getter
    @Setter
    private String albumArtist;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String album;
    @Getter
    @Setter
    private String track;

    public AudioFile(MediaDirectory parent, @NonNull String fileName) {
        super(parent, fileName);
    }
}
