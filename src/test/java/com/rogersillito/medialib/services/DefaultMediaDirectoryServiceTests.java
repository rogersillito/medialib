package com.rogersillito.medialib.services;

import com.rogersillito.medialib.common.OperationResult;
import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.repositories.AudioFileRepository;
import com.rogersillito.medialib.repositories.MediaDirectoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class DefaultMediaDirectoryServiceTests {

    @Mock
    private AudioFileRepository mockAudioFileRepository;
    @Mock
    private MediaDirectoryRepository mockMediaDirectoryRepository;
    @Mock
    private FileBrowser mockFileBrowser;
    private DefaultMediaDirectoryService sut;
    private MediaDirectory mediaDirectory;

    private static final String path = "/some/path/somewhere";

    @BeforeEach
    void arrange() {
        MockitoAnnotations.openMocks(this);
        this.sut = new DefaultMediaDirectoryService(this.mockAudioFileRepository, this.mockMediaDirectoryRepository, this.mockFileBrowser);
    }

    @Test
    void whenNestedFilesFoundAtPath_persistsExpectedDirAndFiles() {
        var dirD = new MediaDirectoryBuilder()
                .atRelativePath("d")
                .withAudioFile(audioFile(5))
                .build();

        var dirC = new MediaDirectoryBuilder()
                .atRelativePath("a/b/c")
                .withAudioFile(audioFile(2))
                .withAudioFile(audioFile(3))
                .withAudioFile(audioFile(4))
                .build();

        var dirB = new MediaDirectoryBuilder()
                .atRelativePath("a/b")
                .withDirectory(dirC)
                .build();

        var dirA = new MediaDirectoryBuilder()
                .atRelativePath("a")
                .withAudioFile(audioFile(1))
                .withDirectory(dirB)
                .build();

        mediaDirectory = new MediaDirectoryBuilder()
                .withDirectory(dirA)
                .withDirectory(dirD)
                .build();

        when(mockFileBrowser.getDirectory(path)).thenReturn(OperationResult.successResult(mediaDirectory));

        var result = sut.saveDirectoryStructure(path);

        verify(this.mockMediaDirectoryRepository, times(1))
                .save(argThat(d -> d.equals(mediaDirectory)));
        assertThat(result, equalTo(5));
    }

    AudioFile audioFile(Integer title) {
        AudioFile audioFile = new AudioFile();
        audioFile.setTitle(title.toString());
        return audioFile;
    }

    static class MediaDirectoryBuilder {
        private final MediaDirectory mediaDirectory;

        MediaDirectoryBuilder() {
            this.mediaDirectory = new MediaDirectory();
            this.mediaDirectory.setPath(DefaultMediaDirectoryServiceTests.path); // default
        }

        MediaDirectoryBuilder atRelativePath(String path) {
            this.mediaDirectory.setPath(DefaultMediaDirectoryServiceTests.path + "/" + path);
            return this;
        }

        MediaDirectoryBuilder withDirectory(MediaDirectory directory) {
            this.mediaDirectory.addSubdirectory(directory);
            return this;
        }

        MediaDirectoryBuilder withAudioFile(AudioFile audioFile) {
            this.mediaDirectory.addFile(audioFile);
            return this;
        }

        MediaDirectory build() {
            return this.mediaDirectory;
        }
    }
}
