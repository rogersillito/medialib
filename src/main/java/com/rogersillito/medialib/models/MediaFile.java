package com.rogersillito.medialib.models;

import java.util.UUID;

@SuppressWarnings("unused")
public interface MediaFile {
    @SuppressWarnings("unused")
    String getFilePath();
    UUID getId();
    MediaDirectory getParent();
    String getFileName();
    String getType();
}
