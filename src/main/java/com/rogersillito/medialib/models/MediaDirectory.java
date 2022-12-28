package com.rogersillito.medialib.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MediaDirectory {
    List<MediaDirectory> directories = new ArrayList<>();
    List<? extends MediaFile> files = new ArrayList<>();
    String path;
}
