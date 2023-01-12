package com.rogersillito.medialib.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//TODO: persist directories too
//@Entity
public class MediaDirectory {
//    @Id
//    @Setter(AccessLevel.PRIVATE)
//    private UUID id;
    List<MediaDirectory> directories = new ArrayList<>();
    List<? extends MediaFile> files = new ArrayList<>();
    String path;
}
