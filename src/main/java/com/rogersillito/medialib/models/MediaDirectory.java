package com.rogersillito.medialib.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class MediaDirectory {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Setter(AccessLevel.PRIVATE) //TODO: Hibernate happy with this??
    private UUID id;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE})
//    @ToString.Exclude
//    private MediaDirectory parent;

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaDirectory> directories = new ArrayList<>();

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //TODO: if we support more than just AudioFile, can we have a collection of the base MediaFile type?
    private List<AudioFile> files = new ArrayList<>();

    private String path;
}
