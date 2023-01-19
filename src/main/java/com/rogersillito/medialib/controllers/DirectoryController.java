package com.rogersillito.medialib.controllers;

import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.services.FileSystemUtils;
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
    private final @NonNull FileSystemUtils fileSystemUtils;

    private final @NonNull MediaDirectoryService mediaDirectoryService;

    //TODO: try out testing the controller?

    @GetMapping
    public ResponseEntity<MediaDirectory> get(@RequestParam("path") String path) {
        //TODO: make this not be recursive (only the directory requested)
        var directory = this.mediaDirectoryService.getMediaDirectory(path);
        return directory.map(md -> new ResponseEntity<>(md, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("refresh")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> refresh(@RequestParam("path") String path) {
        if (!this.fileSystemUtils.canWalk(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var result = mediaDirectoryService.saveDirectoryStructure(path);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
