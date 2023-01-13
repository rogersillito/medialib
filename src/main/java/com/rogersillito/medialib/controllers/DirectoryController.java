package com.rogersillito.medialib.controllers;

import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.services.FileBrowser;
import com.rogersillito.medialib.services.MediaDirectoryService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/directories")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectoryController {
    private final @NonNull FileBrowser fileBrowserService;

    private final @NonNull MediaDirectoryService mediaDirectoryService;

    //TODO: try out testing the controller?

    @GetMapping
    public ResponseEntity<MediaDirectory> get(@RequestParam("path") String path) {
        var directory = this.fileBrowserService.getDirectory(path);
        if (directory.isSuccess()) {
            return new ResponseEntity<>(directory.getData(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("refresh")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> refresh(@RequestParam("path") String path) {
        //TODO: handle non-existent dir
        var result = mediaDirectoryService.saveDirectoryStructure(path);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
