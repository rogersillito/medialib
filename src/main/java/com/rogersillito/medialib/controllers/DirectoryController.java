package com.rogersillito.medialib.controllers;

import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.services.FileBrowser;
import com.rogersillito.medialib.services.MediaDirectoryService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/directories")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectoryController {
    private final @NonNull FileBrowser fileBrowserService;

    private final @NonNull MediaDirectoryService mediaDirectoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MediaDirectory get(@RequestParam("path") String path) {
        System.out.println(path);
        var directory = this.fileBrowserService.getDirectory(path);
        if (directory.isSuccess()) {
            //TODO: move this elsewhere!
            mediaDirectoryService.SaveDirectoryStructure(directory.getData());
            return directory.getData();
        }
        //TODO: appropriate response code + return value
        return null;
    }
}
