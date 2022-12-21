package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.MediaDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class MediaDirectoryWalkerTests {

    private MediaDirectoryWalker sut;

    @Mock
    FileSystemUtils mockedFileSystemUtils;
    private final String path = "/some/path/somewhere";


    @BeforeEach
    void arrange() {
        MockitoAnnotations.openMocks(this);
        this.sut = new MediaDirectoryWalker(mockedFileSystemUtils);
    }

    @Test
    public void whenNoFilesOrDirectoriesFound_returnsEmptyMediaDirectory() {
        when(mockedFileSystemUtils.canWalk(path)).thenReturn(true);
        when(mockedFileSystemUtils.getDirectories(path)).thenReturn(Collections.emptyList());
        when(mockedFileSystemUtils.getFiles(path)).thenReturn(Collections.emptyList());

        var result = sut.walk(path);
        assertTrue(result.isPresent());
        assertEquals(path, result.get().getPath());
        assertEquals(0, result.get().getDirectories().size());
        assertEquals(0, result.get().getFiles().size());
    }

    @Test
    public void whenFilesAndEmptyDirsFoundInNestedDirs_returnsMediaDirectoryExcludingEmptyDirs() {
        when(mockedFileSystemUtils.canWalk(path)).thenReturn(true);
        when(mockedFileSystemUtils.getDirectories(path))
                //TODO: fix this test - consecutive call config not working yet: https://javadoc.io/static/org.mockito/mockito-core/4.10.0/org/mockito/Mockito.html#10
                .thenReturn(List.of(createFile("A")), List.of(createFile("B")), List.of(createFile("C")), Collections.<File>emptyList());
//                .thenReturn(List.of(createFile("A")))
//                .thenReturn(List.of(createFile("B")))
//                .thenReturn(List.of(createFile("C")))
//                .thenReturn(Collections.emptyList());
        when(mockedFileSystemUtils.getFiles(path))
                .thenReturn(List.of(createFile("1.mp3"),createFile("2.mp3")))
                .thenReturn(List.of(createFile("A/1.mp3"),createFile("A/2.mp3")))
                .thenReturn(Collections.emptyList());

        var result = sut.walk(path);
        assertTrue(result.isPresent());
        MediaDirectory topDir = result.get();
        assertEquals(2, topDir.getFiles().size());
        assertEquals(1, topDir.getDirectories().size());
        assertTrue(topDir.getDirectories().stream().findFirst().isPresent());
        var dirA = topDir.getDirectories().stream().findFirst().get();
        assertEquals(path + "/A", dirA.getPath());
        assertEquals(2, dirA.getFiles().size());
        assertEquals(0, dirA.getDirectories().size());
    }

    File createFile(String path) {
        return new File(this.path + "/" + path);
    }
}
