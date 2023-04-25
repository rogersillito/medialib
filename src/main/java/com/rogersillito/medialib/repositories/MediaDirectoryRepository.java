package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.MediaDirectory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaDirectoryRepository extends JpaRepository<MediaDirectory, UUID>, MediaDirectoryRepositoryCustom {
    MediaDirectory findByPath(String path);
}
