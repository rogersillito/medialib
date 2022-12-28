package com.rogersillito.medialib.services;

import java.io.File;
import java.util.List;
import java.util.StringJoiner;

public interface FileSystemUtils {
    List<File> getFiles(String dirPath);

    boolean canWalk(String dirPath);

    List<File> getDirectories(String dirPath);

    static String joinPath(String... pathElements) {
        var joiner = new StringJoiner(File.separator);
        for (String pathElement:
                pathElements) {
            joiner.add(pathElement);
        }
        return joiner.toString();
    }
}
