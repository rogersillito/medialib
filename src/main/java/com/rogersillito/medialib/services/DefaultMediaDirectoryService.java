package com.rogersillito.medialib.services;

import com.rogersillito.medialib.dtos.MediaDirectoryWithRelations;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.projections.AudioFileInfo;
import com.rogersillito.medialib.repositories.AudioFileRepository;
import com.rogersillito.medialib.repositories.MediaDirectoryRepository;
import jakarta.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        var dirToPersist = getRootDirectory(directory);
        this.mediaDirectoryRepository.save(dirToPersist);
        return getTotalFileCount(dirToPersist);
    }

    @Override
    public void deleteDirectoryStructure(String path) {
        var existingDirectory = this.mediaDirectoryRepository.findByPath(path);
        if (existingDirectory != null) {
            MediaDirectory parent = existingDirectory.getParent();
            if (parent != null) {
                parent.removeSubdirectory(existingDirectory);
                this.mediaDirectoryRepository.save(existingDirectory);
                this.mediaDirectoryRepository.save(parent);
            }
            this.mediaDirectoryRepository.delete(existingDirectory);
            this.mediaDirectoryRepository.flush();
        }
    }

    @Override
    public Optional<MediaDirectoryWithRelations> getMediaDirectory(String path) {
        CompletableFuture<MediaDirectoryWithRelations> directoriesFuture =
                CompletableFuture.supplyAsync(() -> this.mediaDirectoryRepository.findWithDirectoryRelationsByPath(path));
        CompletableFuture<List<AudioFileInfo>> audioFilesFuture =
                CompletableFuture.supplyAsync(() -> this.audioFileRepository.findAllByParentPath(path));

        CompletableFuture.allOf(directoriesFuture, audioFilesFuture).join();
        var mediaDirectory = directoriesFuture.join();
        var files = audioFilesFuture.join();
        if (mediaDirectory == null) {
            return Optional.empty();
        }
        mediaDirectory.setFiles(files.stream().toList());

        return Optional.of(mediaDirectory);
    }

    private int getTotalFileCount(MediaDirectory directory) {
        int saveCount = directory.getFiles().size();
        for (var subDirectory :
                directory.getDirectories()) {
            saveCount += getTotalFileCount(subDirectory);
        }
        return saveCount;
    }

    /**
     * Checks if the {@code MediaDirectory} being persisted already has a
     * persisted parent entry.   If it does, create a new parent/child
     * link to associate the two.  Then return this parent as the root object
     * to persist
     **/
    private MediaDirectory getRootDirectory(MediaDirectory directory) {
        var parentPath = Paths.get(directory.getPath()).getParent().toString();
        var existingParent = this.mediaDirectoryRepository.findByPath(parentPath);
        if (existingParent == null) {
            return directory;
        }
        existingParent.addSubdirectory(directory);
        return existingParent;
    }
}
