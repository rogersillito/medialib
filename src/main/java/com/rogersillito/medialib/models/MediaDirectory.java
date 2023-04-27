package com.rogersillito.medialib.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class MediaDirectory {

    @Override
    public String toString() {
        return "%s: %s (%s)".formatted(this.getClass().getSimpleName(), getPath(), getId());
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    @Setter(AccessLevel.PRIVATE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @Setter(AccessLevel.PRIVATE)
    private MediaDirectory parent;

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parent")
    @Setter(AccessLevel.PRIVATE)
    private List<MediaDirectory> directories = new ArrayList<>();

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parent")
    //TODO: if we support more than just AudioFile, can we have a collection of the base MediaFile type?
    @Setter(AccessLevel.PRIVATE)
    private List<AudioFile> files = new ArrayList<>();

    @NonNull
    @Column(unique = true)
    private String path;

    public void addSubdirectory(MediaDirectory subdirectory) {
        if (this.directories.contains(subdirectory)) {
            return;
        }
        this.directories.add(subdirectory);
        subdirectory.setParent(this);
    }

    public void removeSubdirectory(MediaDirectory subdirectory) {
        if (!this.directories.contains(subdirectory)) {
            return;
        }
        this.directories.remove(subdirectory);
        subdirectory.setParent(null);
    }

    public void addFile(AudioFile audioFile) {
        if (this.files.contains(audioFile)) {
            return;
        }
        this.files.add(audioFile);
        audioFile.setParent(this);
    }
}
