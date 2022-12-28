package com.rogersillito.medialib.models;

import lombok.NonNull;

public class AudioFile extends MediaFile {
    public AudioFile(MediaDirectory parent, @NonNull String fileName) {
        super(parent, fileName);
    }

    //TODO: need some ID3 props here...
}
