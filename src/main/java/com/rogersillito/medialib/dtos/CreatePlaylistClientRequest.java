package com.rogersillito.medialib.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreatePlaylistClientRequest {
    @NotBlank
    String name;

    @NotEmpty
    List<UUID> entries;
}