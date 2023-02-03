package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.dtos.MediaDirectoryClientResponse;

public interface MediaDirectoryRepositoryCustom {
    MediaDirectoryClientResponse findMediaDirectoryClientResponseByPath(String path);
}
