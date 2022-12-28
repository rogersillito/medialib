package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;

import java.io.File;

public interface AudioFileFactory {
    AudioFile create(MediaDirectory parent, File file);
}
