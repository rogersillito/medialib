package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.MediaDirectory;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MediaDirectoryRepository extends CrudRepository<MediaDirectory, UUID> {
    MediaDirectory findByPath(String path);
}
