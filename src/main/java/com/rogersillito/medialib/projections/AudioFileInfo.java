package com.rogersillito.medialib.projections;

import java.util.UUID;

/**
 * A Projection for the {@link com.rogersillito.medialib.models.AudioFile} entity
 */
public interface AudioFileInfo {
    UUID getId();

    String getFileName();
}