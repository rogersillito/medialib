package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.models.MediaFile;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class MediaDirectoryWalkerTests {

    private MediaDirectoryWalker sut;

    @Mock
    FileSystemUtils mockedFileSystemUtils;
    private final String basePath = FileSystemUtils.joinPath("some", "path", "somewhere");
    private StubFactory stubFactory;

    static class StubFactory implements AudioFileFactory {

        @Getter
        int callCount;

        @Override
        public Optional<AudioFile> create(MediaDirectory parent, File file) {
            callCount += 1;
            if (file.getName().contains("FAIL")) {
                return Optional.empty();
            }
            AudioFile audioFile = new AudioFile();
            parent.addFile(audioFile);
            audioFile.setFileName(file.getName());
            return Optional.of(audioFile);
        }
    }

    @BeforeEach
    void arrange() {
        MockitoAnnotations.openMocks(this);

        this.stubFactory = new StubFactory();
        this.sut = new MediaDirectoryWalker(this.stubFactory, mockedFileSystemUtils);
    }

    @Test
    public void whenPathInvalid_returnsEmpty() {
        when(mockedFileSystemUtils.canWalk(basePath)).thenReturn(false);
        var result = sut.walk(basePath);
        assertTrue(result.isEmpty());
        assertEquals(0, this.stubFactory.getCallCount());
    }

    @Test
    public void whenNoFilesOrDirectoriesFound_returnsMediaDirectoryWithNoContent() {
        when(mockedFileSystemUtils.canWalk(basePath)).thenReturn(true);
        when(mockedFileSystemUtils.getDirectories(basePath)).thenReturn(Collections.emptyList());
        when(mockedFileSystemUtils.getFiles(basePath)).thenReturn(Collections.emptyList());

        var result = sut.walk(basePath);
        assertTrue(result.isPresent());
        assertEquals(basePath, result.get().getPath());

        assertEquals(0, result.get().getDirectories().size());
        assertEquals(0, result.get().getFiles().size());
        assertEquals(0, this.stubFactory.getCallCount());
    }

    @Test
    public void whenRuntimeExceptionCreatingFile_returnsMediaDirectorySkippingFile() {
        when(mockedFileSystemUtils.canWalk(basePath)).thenReturn(true);
        when(mockedFileSystemUtils.getDirectories(basePath)).thenReturn(Collections.emptyList());
        when(mockedFileSystemUtils.getFiles(basePath))
                .thenReturn(List.of(
                        fileOrDirAtPath("1.mp3"),
                        fileOrDirAtPath("FAIL.mp3"),
                        fileOrDirAtPath("3.mp3")));

        var result = sut.walk(basePath);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getPath(), is(equalTo(basePath)));
        assertThat(result.get().getDirectories(), is(empty()));
        assertThat(result.get().getFiles(), hasSize(2));
        assertThat(this.stubFactory.getCallCount(), is(equalTo(3)));
        assertThat(result.get().getFiles().stream().map(MediaFile::getFileName).toList(), not(hasItem("FAIL.mp3")));
    }

    @Test
    public void whenFilesAndEmptyDirsFoundInNestedDirs_returnsMediaDirectoryExcludingEmptyDirs() {
        /*
        /some/path/somewhere
            /some/path/somewhere/A
                /some/path/somewhere/A/C
                    /1c.mp3
                    /2c.mp3
            /some/path/somewhere/B
                /some/path/somewhere/D
            /1.mp3
            /2.mp3
        */
        when(mockedFileSystemUtils.canWalk(anyString()))
                .thenReturn(true);

        when(mockedFileSystemUtils.getDirectories(basePath))
                .thenReturn(List.of(fileOrDirAtPath("A"), fileOrDirAtPath("B")));
        when(mockedFileSystemUtils.getDirectories(FileSystemUtils.joinPath(basePath, "A")))
                .thenReturn(List.of(fileOrDirAtPath("A", "C")));
        when(mockedFileSystemUtils.getDirectories(FileSystemUtils.joinPath(basePath, "A", "C")))
                .thenReturn(List.of());
        when(mockedFileSystemUtils.getDirectories(FileSystemUtils.joinPath(basePath, "B")))
                .thenReturn(List.of(fileOrDirAtPath("B", "D")));
        when(mockedFileSystemUtils.getDirectories(FileSystemUtils.joinPath(basePath, "B", "D")))
                .thenReturn(List.of());

        when(mockedFileSystemUtils.getFiles(basePath))
                .thenReturn(List.of(
                        fileOrDirAtPath("1.mp3"),
                        fileOrDirAtPath("2.mp3")));
        when(mockedFileSystemUtils.getFiles(FileSystemUtils.joinPath(basePath, "A"))).thenReturn(List.of());
        when(mockedFileSystemUtils.getFiles(FileSystemUtils.joinPath(basePath, "B"))).thenReturn(List.of());
        when(mockedFileSystemUtils.getFiles(FileSystemUtils.joinPath(basePath, "A", "C")))
                .thenReturn(List.of(
                        fileOrDirAtPath("A", "C", "1c.mp3"),
                        fileOrDirAtPath("A", "C", "2c.mp3")));
        when(mockedFileSystemUtils.getFiles(FileSystemUtils.joinPath(basePath, "B", "D"))).thenReturn(List.of());

        var result = sut.walk(basePath);
        assertTrue(result.isPresent());
        MediaDirectory topDir = result.get();
        assertEquals(2, topDir.getFiles().size());
        assertEquals(1, topDir.getDirectories().size());
        assertTrue(topDir.getDirectories().stream().findFirst().isPresent());

        var dirA = topDir.getDirectories().stream().findFirst().get();
        assertEquals(FileSystemUtils.joinPath(basePath, "A"), dirA.getPath());
        assertEquals(0, dirA.getFiles().size());
        assertEquals(1, dirA.getDirectories().size());
        assertTrue(dirA.getDirectories().stream().findFirst().isPresent());

        var dirC = dirA.getDirectories().stream().findFirst().get();
        assertEquals(FileSystemUtils.joinPath(basePath, "A", "C"), dirC.getPath());
        assertEquals(2, dirC.getFiles().size());
        assertEquals(0, dirC.getDirectories().size());

        assertEquals(4, this.stubFactory.getCallCount());
    }

    File fileOrDirAtPath(String... pathElements) {
        var joiner = new StringJoiner(File.separator);
        joiner.add(this.basePath);
        for (String pathElement:
                pathElements) {
            joiner.add(pathElement);
        }
        return new File(joiner.toString());
    }
}
