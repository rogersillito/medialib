package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.dtos.MediaDirectoryClientResponse;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MediaDirectoryRepositoryCustomImpl implements MediaDirectoryRepositoryCustom {
// a custom fragment repository that's pulled into MediaDirectoryRepository via the magic of spring data
    private final EntityManager entityManager;

    @Override
    public MediaDirectoryClientResponse findMediaDirectoryClientResponseByPath(String path) {
        // use JPQL querying API to get joined results and the return as a structured DTO
        String jpqlQuery = """
                SELECT  md.id AS id,
                        md.path AS path,
                        pd.id AS parentId,
                        pd.path AS parentPath,
                        af.id AS fileId,
                        af.fileName AS fileName,
                        cd.id AS childDirId,
                        cd.path AS childDirPath
                FROM MediaDirectory md
                LEFT JOIN MediaDirectory pd ON pd.id = md.parent.id
                LEFT JOIN AudioFile af ON af.parent.id = md.id
                LEFT JOIN MediaDirectory cd ON cd.parent.id = md.id
                WHERE md.path = ?1
                """;
        TypedQuery<Tuple> query = entityManager
                .createQuery(jpqlQuery, Tuple.class)
                .setParameter(1, path);
        try {
            var res = query.getResultList();
            if (res.isEmpty()) {
                return null;
            }
            return convertResult(res);
        } catch (NoResultException nre) {
            return null;
        }
    }

    private MediaDirectoryClientResponse convertResult(List<Tuple> results) {
        var dirId = (UUID)results.get(0).get("id");
        var dirPath = (String)results.get(0).get("path");

        MediaDirectoryClientResponse.Directory parent = null;
        if (results.get(0).get("parentId") != null) {
            var parentId = (UUID) results.get(0).get("parentId");
            var parentPath = (String) results.get(0).get("parentPath");
            parent = new MediaDirectoryClientResponse.Directory(parentId, parentPath);
        }

        var files = results.stream().filter(r -> r.get("fileId") != null).map(r -> {
            var fileId = (UUID)r.get("fileId");
            var fileName = (String)r.get("fileName");
            return new MediaDirectoryClientResponse.File(fileId, fileName);
        }).toList();

        var childDirs = results.stream().filter(r -> r.get("childDirId") != null).map(r -> {
            var childDirId = (UUID)r.get("childDirId");
            var childDirPath = (String)r.get("childDirPath");
            return new MediaDirectoryClientResponse.Directory(childDirId, childDirPath);
        }).toList();

        return new MediaDirectoryClientResponse(dirId, dirPath, parent, files, childDirs);
    }
}
