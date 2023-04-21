package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaDirectoryWalker implements DirectoryWalker<MediaDirectory> {
    @Autowired
    final AudioFileFactory audioFileFactory;
    @Autowired
    final FileSystemUtils fileSystemUtils;
    public Optional<MediaDirectory> walk(String path) {
        var forkJoinPool = new ForkJoinPool();
        var dirWalkTask = new DirWalkTask(path, 0);
        return forkJoinPool.invoke(dirWalkTask);
    }

    @RequiredArgsConstructor
    class DirWalkTask extends RecursiveTask<Optional<MediaDirectory>>
    {
        private final String path;
        private final int depth;

        @Override
        protected Optional<MediaDirectory> compute() {
            log.debug("Enumerating directory: " + this.path);
            if (!fileSystemUtils.canWalk(this.path)) {
                return Optional.empty();
            }
            var thisDirectory = new MediaDirectory();
            thisDirectory.setPath(this.path);
            List<MediaDirectory> subDirectories = getSubDirectories();
            List<AudioFile> audioFiles = getAudioFiles(thisDirectory);
            if (this.depth == 0 || audioFiles.size() > 0 || subDirectories.size() > 0) {
                thisDirectory.setDirectories(subDirectories);
                thisDirectory.setFiles(audioFiles);
                subDirectories.forEach(sd -> sd.setParent(thisDirectory));
                audioFiles.forEach(sd -> sd.setParent(thisDirectory));
                return Optional.of(thisDirectory);
            }
            return Optional.empty();
        }

        private List<AudioFile> getAudioFiles(MediaDirectory thisDirectory) {
            List<AudioFile> audioFiles = new ArrayList<>();
            for (File file :
                    fileSystemUtils.getFiles(this.path)) {
                audioFileFactory.create(thisDirectory, file).ifPresent(audioFiles::add);
            }
            return audioFiles;
        }

        private List<MediaDirectory> getSubDirectories() {
            List<DirWalkTask> subDirTasks = new ArrayList<>();
            var subDirDepth = this.depth + 1;
            for (File directory :
                    fileSystemUtils.getDirectories(this.path)) {
                var subDirWalkTask = new DirWalkTask(directory.getPath(), subDirDepth);
                subDirTasks.add(subDirWalkTask);
            }
            RecursiveTask.invokeAll(subDirTasks);
            return subDirTasks.stream()
                    .map(RecursiveTask::join)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }
    }
}
