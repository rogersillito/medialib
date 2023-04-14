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
    private List<AudioFileClientResponse> files;
    //TODO: include dirs as well..
    @AllArgsConstructor
    @Getter
    public static class AudioFileClientResponse {
        private UUID id;
        private String fileName;
    }
}
