package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class MediaDirectoryWalkerTests {

    private MediaDirectoryWalker sut;

    @Mock
    AudioFileFactory mockedAudioFileFactory;
    @Mock
    FileSystemUtils mockedFileSystemUtils;
    private final String path = FileSystemUtils.joinPath("some", "path", "somewhere");


    @BeforeEach
    void arrange() {
        MockitoAnnotations.openMocks(this);
        //TODO: pass the received MediaDirectory as the AudioFile's ctr param
        when(mockedAudioFileFactory.create(any(MediaDirectory.class), any(File.class)))
                .thenReturn(new AudioFile(null, "file.mp3"));

        this.sut = new MediaDirectoryWalker(mockedAudioFileFactory, mockedFileSystemUtils);
    }

    @Test
    public void whenPathInvalid_returnsEmpty() {
        when(mockedFileSystemUtils.canWalk(path)).thenReturn(false);
        var result = sut.walk(path);
        assertTrue(result.isEmpty());
    }

    @Test
    public void whenNoFilesOrDirectoriesFound_returnsMediaDirectoryWithNoContent() {
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
        /*
        /some/path/somewhere
            /some/path/somewhere/A
                /some/path/somewhere/A/C
                /1a.mp3
                /2a.mp3
            /some/path/somewhere/B
            /1.mp3
            /2.mp3
        */
        when(mockedFileSystemUtils.canWalk(anyString()))
                .thenReturn(true);

        when(mockedFileSystemUtils.getDirectories(path))
                .thenReturn(List.of(fileOrDirAtPath("A"), fileOrDirAtPath("B")));
        when(mockedFileSystemUtils.getDirectories(FileSystemUtils.joinPath(path, "A")))
                .thenReturn(List.of(fileOrDirAtPath("A", "C")));
        when(mockedFileSystemUtils.getDirectories(FileSystemUtils.joinPath("A", "B"))).thenReturn(List.of());
        when(mockedFileSystemUtils.getDirectories(FileSystemUtils.joinPath("A", "C"))).thenReturn(List.of());

        when(mockedFileSystemUtils.getFiles(path))
                .thenReturn(List.of(
                        fileOrDirAtPath("1.mp3"),
                        fileOrDirAtPath("2.mp3")));
        when(mockedFileSystemUtils.getFiles(FileSystemUtils.joinPath(path, "A")))
                .thenReturn(List.of(
                        fileOrDirAtPath("A", "1a.mp3"),
                        fileOrDirAtPath("A", "2a.mp3")));
        when(mockedFileSystemUtils.getFiles(FileSystemUtils.joinPath(path, "B"))).thenReturn(List.of());
        when(mockedFileSystemUtils.getFiles(FileSystemUtils.joinPath(path, "A", "C"))).thenReturn(List.of());

        var result = sut.walk(path);
        assertTrue(result.isPresent());
        MediaDirectory topDir = result.get();
        assertEquals(2, topDir.getFiles().size());
        assertEquals(1, topDir.getDirectories().size());
        assertTrue(topDir.getDirectories().stream().findFirst().isPresent());

        var dirA = topDir.getDirectories().stream().findFirst().get();
        assertEquals(FileSystemUtils.joinPath(path, "A"), dirA.getPath());
        assertEquals(2, dirA.getFiles().size());
        assertEquals(0, dirA.getDirectories().size());
    }

    File fileOrDirAtPath(String... pathElements) {
        var joiner = new StringJoiner(File.separator);
        joiner.add(this.path);
        for (String pathElement:
                pathElements) {
            joiner.add(pathElement);
        }
        return new File(joiner.toString());
    }
}
