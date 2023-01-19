package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.MediaDirectory;

import java.util.Optional;

public interface MediaDirectoryService {

    int saveDirectoryStructure(String path);
    Optional<MediaDirectory> getMediaDirectory(String path);
}
