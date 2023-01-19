package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.MediaDirectoryClientResponse;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MediaDirectoryRepositoryCustomImpl implements MediaDirectoryRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    public MediaDirectoryClientResponse findMediaDirectoryClientResponseByPath(String path) {
        //TODO: figure out how we can return a list of child file/directory paths (non-nested)
        String jpqlQuery = """
            SELECT
            new com.rogersillito.medialib.models.MediaDirectoryClientResponse(md.Id, md.path)
            FROM MediaDirectory md
            WHERE md.path = ?1""";
        var query = entityManager
                .createQuery(jpqlQuery, MediaDirectoryClientResponse.class)
                .setParameter(1, path);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
