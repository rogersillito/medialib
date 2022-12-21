package com.rogersillito.medialib.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FileSystemUtilsImpl implements FileSystemUtils {
    @Autowired
    final FileFilter audioFileFilter;

    @Override
    public List<File> getFiles(String dirPath) {
        var dir = new File(dirPath);
        return Stream.of(Objects.requireNonNull(dir.listFiles(audioFileFilter)))
                .filter(File::isFile)
                .toList();
    }

    @Override
    public boolean canWalk(String dirPath) {
        var dir = new File(dirPath);
        return dir.exists() && dir.isDirectory();
    }

    @Override
    public List<File> getDirectories(String dirPath) {
        var dir = new File(dirPath);
        return Stream.of(Objects.requireNonNull(dir.listFiles()))
                .filter(File::isDirectory)
                .toList();
    }
}
