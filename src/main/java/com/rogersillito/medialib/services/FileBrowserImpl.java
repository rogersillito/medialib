package com.rogersillito.medialib.services;

import com.rogersillito.medialib.common.OperationResult;
import com.rogersillito.medialib.models.MediaDirectory;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@NoArgsConstructor
public class FileBrowserImpl implements FileBrowser {
    @Autowired
    private DirectoryWalker<MediaDirectory> dirWalker;
    @Override
    public OperationResult<MediaDirectory> getDirectory(String path) {

        Optional<MediaDirectory> directory = dirWalker.walk(path);
        //TODO: test this behaviour
        return directory.map(OperationResult::successResult).orElseGet(() -> OperationResult.failureResult("Directory path either empty or not found"));
    }
}
