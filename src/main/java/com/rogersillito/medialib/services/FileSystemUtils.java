package com.rogersillito.medialib.services;

import java.io.File;
import java.util.List;

public interface FileSystemUtils {
    List<File> getFiles(String dirPath);

    boolean canWalk(String dirPath);

    List<File> getDirectories(String dirPath);
}
