package com.rogersillito.medialib.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rogersillito.medialib.services.FileSystemUtils;
import lombok.*;

@Data
@JsonIgnoreProperties(value = { "parent" })
public abstract class MediaFile {

    final MediaDirectory parent;
    final String fileName;

    String getFilePath() {
        return FileSystemUtils.joinPath(parent.getPath(), fileName);
    }
}
