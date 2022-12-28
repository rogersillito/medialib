package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.models.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaDirectoryWalker implements DirectoryWalker<MediaDirectory> {
    @Autowired
    final FileSystemUtils fileSystemUtils;
    public Optional<MediaDirectory> walk(String path) {
        return walkDirectory(path, 0);
    }

    private Optional<MediaDirectory> walkDirectory(String path, int depth) {
        if (!fileSystemUtils.canWalk(path)) {
            return Optional.empty();
        }
        var subdirectories = new ArrayList<MediaDirectory>();
        var mediaFiles = new ArrayList<MediaFile>();
        var subDirDepth = depth + 1;
        for (File directory :
                fileSystemUtils.getDirectories(path)) {
            walkDirectory(directory.getPath(), subDirDepth).ifPresent(subdirectories::add);
        }
        for (File file :
                fileSystemUtils.getFiles(path)) {
            //TODO: use a factory to create a MediaFile instead (and load ID3 data etc)
            mediaFiles.add(new MediaFile(file));
        }
        if (depth == 0 || mediaFiles.size() > 0 || subdirectories.size() > 0) {
            var thisDirectory = new MediaDirectory();
            thisDirectory.setPath(path);
            thisDirectory.getDirectories().addAll(subdirectories);
            thisDirectory.getFiles().addAll(mediaFiles);
            return Optional.of(thisDirectory);
        }
        return Optional.empty();
    }
}
