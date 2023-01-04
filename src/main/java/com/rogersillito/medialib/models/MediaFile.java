package com.rogersillito.medialib.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rogersillito.medialib.services.FileSystemUtils;
import lombok.*;

@Data
@JsonIgnoreProperties(value = { "parent" })
public abstract class MediaFile {

    final MediaDirectory parent;
    final String fileName;
    private String type;

    //TODO: is there a way to get this json-encoded too?
    String getFilePath() {
        return FileSystemUtils.joinPath(parent.getPath(), fileName);
    }
}
