package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.projections.AudioFileInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface AudioFileRepository extends ListCrudRepository<AudioFile, UUID> {

    @Query("SELECT af.id as id, af.fileName as fileName FROM MediaDirectory md INNER JOIN AudioFile af ON af.parent.id = md.id where md.path = ?1 order by af.fileName ASC")
    List<AudioFileInfo> findAllByParentPath(String path);
}
