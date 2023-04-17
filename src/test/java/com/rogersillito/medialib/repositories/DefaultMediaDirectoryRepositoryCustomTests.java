package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.services.MediaDirectoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Tag("Integration")
public class DefaultMediaDirectoryRepositoryCustomTests {

    @Autowired
    MediaDirectoryRepository mediaDirectoryRepository;

    @Autowired
    MediaDirectoryService mediaDirectoryService;

    AudioFile audioFile(Integer title) {
        AudioFile audioFile = new AudioFile();
        audioFile.setTitle(title.toString());
        audioFile.setFileName(title.toString().concat(".mp3"));
        return audioFile;
    }

    @Test
    void findMediaDirectoryClientResponseByPath_childDirsAndFilesReturnsExpected() {
        // ARRANGE
        var mediaDirectory = new MediaDirectory();
        mediaDirectory.setPath("/some/media/dir1");
        var audioFile1 = audioFile(1);
        var audioFile2 = audioFile(2);

        audioFile1.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile1);
        audioFile2.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile2);

        var parentDirectory = new MediaDirectory();
        parentDirectory.setPath("/some/media");
        parentDirectory.getDirectories().add(mediaDirectory);
        mediaDirectory.setParent(parentDirectory);

        var childDirectory1 = new MediaDirectory();
        childDirectory1.setPath("/some/media/dir1/dir2a");
        mediaDirectory.getDirectories().add(childDirectory1);
        childDirectory1.setParent(mediaDirectory);
        var childDirectory2 = new MediaDirectory();
        childDirectory2.setPath("/some/media/dir1/dir2b");
        mediaDirectory.getDirectories().add(childDirectory2);
        childDirectory2.setParent(mediaDirectory);

        mediaDirectoryService.saveDirectoryStructure(parentDirectory);

        // ACT
        var result = mediaDirectoryRepository.findMediaDirectoryClientResponseByPath("/some/media/dir1");

        // ASSERT
        assertThat(result, is(notNullValue()));
        assertThat(result.getFiles(), hasSize(2));
        assertThat(result.getParent(), is(notNullValue()));
        assertThat(result.getParent().getPath(), is("/some/media"));
        assertThat(result.getDirectories(), hasSize(2));
    }

    @Test
    void findMediaDirectoryClientResponseByPath_childFilesNoDirsReturnsExpected() {
        // ARRANGE
        var mediaDirectory = new MediaDirectory();
        mediaDirectory.setPath("/some/media/dir1");
        var audioFile1 = audioFile(1);
        var audioFile2 = audioFile(2);

        audioFile1.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile1);
        audioFile2.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile2);

        var parentDirectory = new MediaDirectory();
        parentDirectory.setPath("/some/media");
        parentDirectory.getDirectories().add(mediaDirectory);
        mediaDirectory.setParent(parentDirectory);

        mediaDirectoryService.saveDirectoryStructure(parentDirectory);

        // ACT
        var result = mediaDirectoryRepository.findMediaDirectoryClientResponseByPath("/some/media/dir1");

        // ASSERT
        assertThat(result, is(notNullValue()));
        assertThat(result.getFiles(), hasSize(2));
        assertThat(result.getParent(), is(notNullValue()));
        assertThat(result.getParent().getPath(), is("/some/media"));
    }

    @Test
    void findMediaDirectoryClientResponseByPath_noParentReturnsExpected() {
        // ARRANGE
        var mediaDirectory = new MediaDirectory();
        mediaDirectory.setPath("/some/media/dir1");
        var audioFile1 = audioFile(1);
        var audioFile2 = audioFile(2);

        audioFile1.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile1);
        audioFile2.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile2);

        mediaDirectoryService.saveDirectoryStructure(mediaDirectory);

        // ACT
        var result = mediaDirectoryRepository.findMediaDirectoryClientResponseByPath("/some/media/dir1");

        // ASSERT
        assertThat(result, is(notNullValue()));
        assertThat(result.getFiles(), hasSize(2));
        assertThat(result.getParent(), is(nullValue()));
    }
}
