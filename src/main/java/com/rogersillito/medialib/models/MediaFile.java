package com.rogersillito.medialib.models;

import lombok.Data;
import lombok.NonNull;

import java.io.File;

@Data
public class MediaFile {
    //TODO: need some ID3 props here...
    @NonNull File file;
}
