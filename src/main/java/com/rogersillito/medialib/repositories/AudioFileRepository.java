package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.AudioFile;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AudioFileRepository extends CrudRepository<AudioFile, UUID> {
}
