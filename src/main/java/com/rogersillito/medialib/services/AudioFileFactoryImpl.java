package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AudioFileFactoryImpl implements AudioFileFactory {
    @Override
    public AudioFile create(MediaDirectory parent, File file) {
        var audioFile = new AudioFile(parent, file.getName());
        return audioFile;
    }
}
