package com.rogersillito.medialib.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rogersillito.medialib.services.FileSystemUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@JsonIgnoreProperties(value = { "parent" })
public class AudioFile implements MediaFile {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Setter(AccessLevel.PRIVATE) //TODO: Hibernate happy with this??
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Cascade({CascadeType.SAVE_UPDATE})
    @ToString.Exclude
    private MediaDirectory parent;
    @Column(length=3000)
    private String fileName;

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
