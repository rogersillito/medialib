package com.rogersillito.medialib.services;

import java.util.Optional;

public interface DirectoryWalker<T> {
    Optional<T> walk(String path);
}
