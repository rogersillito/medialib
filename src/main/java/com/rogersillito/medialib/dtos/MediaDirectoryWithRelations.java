package com.rogersillito.medialib.dtos;

import com.rogersillito.medialib.projections.AudioFileInfo;
import lombok.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@ToString
@Getter
public class MediaDirectoryWithRelations {
    private final UUID id;
    private final String path;
    private final Directory parent;
    private final List<Directory> directories;

    @Setter
    private List<AudioFileInfo> files;

    @AllArgsConstructor
    @Getter
    public static class Directory {
        private UUID id;
        private String path;
    }
}
