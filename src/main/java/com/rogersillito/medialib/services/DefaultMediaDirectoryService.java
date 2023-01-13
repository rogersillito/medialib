package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.models.MediaFile;
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
    private final @NonNull FileBrowser fileBrowser;

    @Override
    public int saveDirectoryStructure(String path) {
        //TODO: instead update if files already persisted
        var directory = this.fileBrowser.getDirectory(path);
        if (directory.isSuccess()) {
            return persistDirectory(directory.getData());
        }
        //TODO: return - inserted, updated, deleted?
        return 0;
    }

    private int persistDirectory(MediaDirectory directory) {
        int saveCount = persistAudioFiles(directory.getFiles());
        for (var subDirectory :
                directory.getDirectories()) {
            saveCount += persistDirectory(subDirectory);
        }
        return saveCount;
    }

    private int persistAudioFiles(List<MediaFile> mediaFiles) {
        var files = mediaFiles.stream()
                .filter(f -> f instanceof AudioFile)
                .map(AudioFile.class::cast)
                .toList();
        this.audioFileRepository.saveAll(files);
        return files.size();
    }
}
