package com.rogersillito.medialib.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MediaDirectoryClientResponse {
    private UUID id;
    private String path;
}
