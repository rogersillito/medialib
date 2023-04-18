package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.dtos.MediaDirectoryWithRelations;

public interface MediaDirectoryRepositoryCustom {
    MediaDirectoryWithRelations findWithDirectoryRelationsByPath(String path);
}
