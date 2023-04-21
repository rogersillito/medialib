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
    private MediaDirectory parent;

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parent")
    private List<MediaDirectory> directories = new ArrayList<>();

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //TODO: if we support more than just AudioFile, can we have a collection of the base MediaFile type?
    private List<AudioFile> files = new ArrayList<>();

    @NonNull
    private String path;
}
