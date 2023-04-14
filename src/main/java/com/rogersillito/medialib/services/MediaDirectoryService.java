package com.rogersillito.medialib.services;

import com.rogersillito.medialib.dtos.MediaDirectoryClientResponse;
import com.rogersillito.medialib.models.MediaDirectory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MediaDirectoryService {

    @Transactional
    int saveDirectoryStructure(String path);

    @Transactional
    int saveDirectoryStructure(MediaDirectory directory);

    Optional<MediaDirectoryClientResponse> getMediaDirectory(String path);
}
