package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.models.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MediaDirectoryWalker implements DirectoryWalker<MediaDirectory> {
    @Autowired
    final FileFilter audioFileFilter;
    public Optional<MediaDirectory> walk(String path) {
        var dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return Optional.empty();
        }
        var subdirectories = new ArrayList<MediaDirectory>();
        var mediaFiles = new ArrayList<MediaFile>();
        for (File directory :
                Stream.of(Objects.requireNonNull(dir.listFiles()))
                        .filter(File::isDirectory)
                        .toList()) {
            walk(directory.getPath()).ifPresent(subdirectories::add);
        }
        for (File file :
                Stream.of(Objects.requireNonNull(dir.listFiles(audioFileFilter)))
                        .filter(File::isFile)
                        .toList()) {
            System.out.println(file.getPath());
            //TODO: use a factory to create a MediaFile instead (and load ID3 data etc)
            mediaFiles.add(new MediaFile(file));
        }
        if (mediaFiles.size() > 0 || subdirectories.size() > 0) {
            var thisDirectory = new MediaDirectory();
            thisDirectory.setPath(path);
            thisDirectory.getDirectories().addAll(subdirectories);
            thisDirectory.getFiles().addAll(mediaFiles);
            return Optional.of(thisDirectory);
        }
        //TODO: if it's the top-level dir, allow it to return empty
        return Optional.empty();
    }
}
