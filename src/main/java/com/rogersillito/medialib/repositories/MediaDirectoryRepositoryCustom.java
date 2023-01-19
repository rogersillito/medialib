package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.MediaDirectoryClientResponse;

public interface MediaDirectoryRepositoryCustom {
    MediaDirectoryClientResponse findMediaDirectoryClientResponseByPath(String path);
}
