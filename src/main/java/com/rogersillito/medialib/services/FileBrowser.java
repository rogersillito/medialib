package com.rogersillito.medialib.services;

import com.rogersillito.medialib.common.OperationResult;
import com.rogersillito.medialib.models.MediaDirectory;

public interface FileBrowser {
    OperationResult<MediaDirectory> getDirectory(String path);
}
