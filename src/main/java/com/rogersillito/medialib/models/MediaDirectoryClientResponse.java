package com.rogersillito.medialib.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class MediaDirectoryClientResponse {
    private UUID id;
    private String path;
    private List<AudioFile> files;
    //TODO: include dirs as well..
    @AllArgsConstructor
    @Getter
    public static class AudioFile {
        private UUID id;
        private String fileName;
    }
}
