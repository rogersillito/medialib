package com.rogersillito.medialib.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class MediaDirectory {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Setter(AccessLevel.PRIVATE) //TODO: NHibernate happy with this??
    private UUID id;

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaDirectory> directories = new ArrayList<>();

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //TODO: if we support more than just AudioFile, can we have a collection of the base MediaFile type?
    private List<AudioFile> files = new ArrayList<>();

    private String path;
}
