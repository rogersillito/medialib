package com.rogersillito.medialib.services;

import com.rogersillito.medialib.dtos.MediaDirectoryWithRelations;
import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.repositories.AudioFileRepository;
import com.rogersillito.medialib.repositories.MediaDirectoryRepository;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultMediaDirectoryService implements MediaDirectoryService {

    private final @NonNull AudioFileRepository audioFileRepository;
    private final @NonNull MediaDirectoryRepository mediaDirectoryRepository;
    private final @NonNull FileBrowser fileBrowser;

    @Override
    @Transactional
    public int saveDirectoryStructure(String path) {
        var directory = this.fileBrowser.getDirectory(path);
        if (directory.isSuccess()) {
            return saveDirectoryStructure(directory.getData());
        }
        //TODO: return - inserted, updated, deleted?
        return 0;
    }

    @Override
    @Transactional
    public int saveDirectoryStructure(MediaDirectory directory) {
        //TODO: instead update if files already persisted
        deleteDirectoryStructure(directory.getPath());
        return persistDirectory(directory);
    }

    @Override
    public void deleteDirectoryStructure(String path) {
        var existingDirectory = this.mediaDirectoryRepository.findByPath(path);
        if (existingDirectory != null) {
            this.mediaDirectoryRepository.delete(existingDirectory);
        }
    }

    @Override
    public Optional<MediaDirectoryWithRelations> getMediaDirectory(String path) {
        var mediaDirectory = this.mediaDirectoryRepository.findWithDirectoryRelationsByPath(path);
        if (mediaDirectory == null) {
            return Optional.empty();
        }

        var files = this.audioFileRepository.findAllByParentPath(path);
        mediaDirectory.setFiles(files.stream().toList());

        return Optional.of(mediaDirectory);
    }

    private int persistDirectory(MediaDirectory directory) {
        this.mediaDirectoryRepository.save(directory);
        int saveCount = persistAudioFiles(directory.getFiles());
        for (var subDirectory :
                directory.getDirectories()) {
            saveCount += persistDirectory(subDirectory);
        }
        return saveCount;
    }

    private int persistAudioFiles(List<AudioFile> audioFiles) {
        this.audioFileRepository.saveAll(audioFiles);
        return audioFiles.size();
    }
}
