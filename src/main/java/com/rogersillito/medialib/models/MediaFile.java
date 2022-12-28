package com.rogersillito.medialib.models;

import com.rogersillito.medialib.services.FileSystemUtils;
import lombok.*;

@Data
public abstract class MediaFile {

    final MediaDirectory parent;
    @NonNull String fileName;

    String getFilePath() {
        return FileSystemUtils.joinPath(parent.getPath(), fileName);
    }
}
