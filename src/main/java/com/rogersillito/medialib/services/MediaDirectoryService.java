package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.MediaDirectoryClientResponse;

import java.util.Optional;

public interface MediaDirectoryService {

    int saveDirectoryStructure(String path);
    Optional<MediaDirectoryClientResponse> getMediaDirectory(String path);
}
