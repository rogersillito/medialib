package com.rogersillito.medialib.controllers;

import com.rogersillito.medialib.common.ApiError;
import com.rogersillito.medialib.dtos.CreatePlaylistClientRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/playlist")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlaylistController {
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreatePlaylistClientRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //TODO: global error handling?
            var errors = new ArrayList<String>();
            for (FieldError error:
                 bindingResult.getFieldErrors()) {
                errors.add(String.format("%s %s", error.getField(), error.getDefaultMessage()));
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.of(HttpStatus.BAD_REQUEST, CreatePlaylistClientRequest.class.getSimpleName() + " invalid", errors));
        }

        //TODO: persist something!
        return new ResponseEntity<>(UUID.randomUUID(), HttpStatus.CREATED);
    }
}
