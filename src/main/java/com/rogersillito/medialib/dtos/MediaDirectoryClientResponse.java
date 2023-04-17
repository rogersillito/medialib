package com.rogersillito.medialib.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class MediaDirectoryClientResponse {
    private UUID id;
    private String path;
    private Directory parent;
    private List<File> files;
    private List<Directory> directories;
    @AllArgsConstructor
    @Getter
    public static class File {
        private UUID id;
        private String fileName;
    }

    @AllArgsConstructor
    @Getter
    public static class Directory {
        private UUID id;
        private String path;
    }
}
