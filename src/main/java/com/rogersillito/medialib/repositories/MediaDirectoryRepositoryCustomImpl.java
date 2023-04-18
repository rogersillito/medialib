package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.dtos.MediaDirectoryWithRelations;
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
    public MediaDirectoryWithRelations findWithDirectoryRelationsByPath(String path) {
        // use JPQL querying API to get joined results and the return as a structured DTO
        String jpqlQuery = """
                SELECT  md.id AS id,
                        md.path AS path,
                        pd.id AS parentId,
                        pd.path AS parentPath,
                        cd.id AS childId,
                        cd.path AS childPath
                FROM MediaDirectory md
                LEFT JOIN MediaDirectory pd ON pd.id = md.parent.id
                LEFT JOIN MediaDirectory cd ON cd.parent.id = md.id
                WHERE md.path = ?1
                ORDER BY cd.path ASC
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

    private MediaDirectoryWithRelations convertResult(List<Tuple> results) {
        var id = (UUID)results.get(0).get("id");
        var path = (String)results.get(0).get("path");

        MediaDirectoryWithRelations.Directory parent = null;
        if (results.get(0).get("parentId") != null) {
            var parentId = (UUID) results.get(0).get("parentId");
            var parentPath = (String) results.get(0).get("parentPath");
            parent = new MediaDirectoryWithRelations.Directory(parentId, parentPath);
        }

        var children = results.stream().filter(r -> r.get("childId") != null).map(r -> {
            var childId = (UUID)r.get("childId");
            var childPath = (String)r.get("childPath");
            return new MediaDirectoryWithRelations.Directory(childId, childPath);
        }).toList();

        return new MediaDirectoryWithRelations(id, path, parent, children);
    }
}
