package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.repositories.AudioFileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultMediaDirectoryService implements MediaDirectoryService {

    private final @NonNull AudioFileRepository audioFileRepository;

    @Override
    public int SaveDirectoryStructure(MediaDirectory mediaDirectory) {
        List<AudioFile> files = mediaDirectory.getFiles().stream().map(mf -> (AudioFile)mf).toList();
        audioFileRepository.saveAll(files);
        return 1;
    }
}
