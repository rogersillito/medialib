package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.MediaDirectoryClientResponse;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
//TODO: needs some test coverage!
public class MediaDirectoryRepositoryCustomImpl implements MediaDirectoryRepositoryCustom {
// a custom fragment repository that's pulled into MediaDirectoryRepository via the magic of spring data
    private final EntityManager entityManager;

    @Override
    public MediaDirectoryClientResponse findMediaDirectoryClientResponseByPath(String path) {
        // use JPQL querying API to get joined results and the return as a structured DTO
        String jpqlQuery = """
                SELECT  md.id AS id,
                        md.path AS path,
                        af.id AS fileId,
                        af.fileName AS fileName
                FROM MediaDirectory md
                LEFT JOIN AudioFile af ON af.parent.id = md.id
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
        var files = results.stream().filter(r -> r.get("fileId") != null).map(r -> {
            var fileId = (UUID)r.get("fileId");
            var fileName = (String)r.get("fileName");
            return new MediaDirectoryClientResponse.AudioFile(fileId, fileName);
        }).toList();
        return new MediaDirectoryClientResponse(dirId, dirPath, files);
    }
}
