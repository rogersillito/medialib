package com.rogersillito.medialib.controllers;

import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.services.FileBrowser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/directories")
public class DirectoryController {
    @Autowired
    private FileBrowser fileBrowserService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MediaDirectory get(@RequestParam("path") String path) {
        System.out.println(path);
        var directory = this.fileBrowserService.getDirectory(path);
        if (directory.isSuccess()) {
            return directory.getData();
        }
        //TODO: appropriate response code + return value
        return null;
    }
}
