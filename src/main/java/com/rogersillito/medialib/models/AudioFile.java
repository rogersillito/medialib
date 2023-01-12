package com.rogersillito.medialib.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rogersillito.medialib.services.FileSystemUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@JsonIgnoreProperties(value = { "parent" })
public class AudioFile implements MediaFile {

    @Id
    @Setter(AccessLevel.PRIVATE)
    private UUID id;

    @NonNull
    @Transient //TODO: persist this relationship
    private MediaDirectory parent;
    @NonNull
    @Column(length=3000)
    private String fileName;

    public AudioFile() {
        this.id = UUID.randomUUID();
    }

    @Override
    @SuppressWarnings("unused")
    public String getFilePath() {
        return FileSystemUtils.joinPath(parent.getPath(), fileName);
    }
    @Column(length=200)
    private String type;
    @Column(length=2000)
    private String artist;
    @Column(length=2000)
    private String albumArtist;
    @Column(length=2000)
    private String title;
    @Column(length=2000)
    private String album;
    @Getter
    private int track;

    public void setTrack(String track) {
        this.track = Integer.parseInt(track);
    }
}
