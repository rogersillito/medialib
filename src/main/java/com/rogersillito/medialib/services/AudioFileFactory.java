package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;

import java.io.File;
import java.util.Optional;

public interface AudioFileFactory {
    Optional<AudioFile> create(MediaDirectory parent, File file) throws RuntimeException;
}
