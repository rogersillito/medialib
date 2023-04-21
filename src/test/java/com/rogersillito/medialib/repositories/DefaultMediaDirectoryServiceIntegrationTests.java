package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.services.FileSystemUtils;
import com.rogersillito.medialib.services.MediaDirectoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Tag("Integration")
public class DefaultMediaDirectoryServiceIntegrationTests {

    @Autowired
    private MediaDirectoryService mediaDirectoryService;
    @Autowired
    private MediaDirectoryRepository mediaDirectoryRepository;
    private final List<MediaDirectory> testDirectories = new ArrayList<>();

    AudioFile createAudioFile(Integer title) {
        AudioFile audioFile = new AudioFile();
        audioFile.setTitle(title.toString());
        audioFile.setFileName(title.toString().concat(".mp3"));
        return audioFile;
    }

    private MediaDirectory createMediaDirectory(String path) {
        var mediaDirectory = new MediaDirectory();
        mediaDirectory.setPath(path);
        testDirectories.add(mediaDirectory);
        return mediaDirectory;
    }

    @AfterEach
    void cleanupTestData() {
        this.testDirectories.forEach(this.mediaDirectoryRepository::delete);
        this.testDirectories.clear();
    }

    @Test
    void getMediaDirectory_childDirsAndFilesReturnsExpected() {
        // ARRANGE
        var parentDirectory = createMediaDirectory("/some/media");
        var mediaDirectory = createMediaDirectory("/some/media/dir1");
        var audioFile1 = createAudioFile(1);
        var audioFile2 = createAudioFile(2);

        audioFile2.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile2);
        audioFile1.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile1);

        parentDirectory.getDirectories().add(mediaDirectory);
        mediaDirectory.setParent(parentDirectory);

        var childDirectory2 = new MediaDirectory();
        childDirectory2.setPath("/some/media/dir1/dir2b");
        mediaDirectory.getDirectories().add(childDirectory2);
        childDirectory2.setParent(mediaDirectory);
        var childDirectory1 = new MediaDirectory();
        childDirectory1.setPath("/some/media/dir1/dir2a");
        mediaDirectory.getDirectories().add(childDirectory1);
        childDirectory1.setParent(mediaDirectory);

        mediaDirectoryService.saveDirectoryStructure(parentDirectory);

        // ACT
        var result = mediaDirectoryService.getMediaDirectory("/some/media/dir1");

        // ASSERT
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(notNullValue()));
        assertThat(result.get().getFiles(), hasSize(2));
        assertThat(result.get().getFiles().get(0).getFileName(), is("1.mp3"));
        assertThat(result.get().getFiles().get(0).getId(), is(notNullValue()));
        assertThat(result.get().getParent(), is(notNullValue()));
        assertThat(result.get().getParent().getPath(), is("/some/media"));
        assertThat(result.get().getParent().getId(), is(notNullValue()));
        assertThat(result.get().getDirectories(), hasSize(2));
        assertThat(result.get().getDirectories().get(1).getPath(), is("/some/media/dir1/dir2b"));
        assertThat(result.get().getDirectories().get(1).getId(), is(notNullValue()));
    }

    @Test
    void getMediaDirectory_childDirsNoFilesReturnsExpected() {
        // ARRANGE
        var parentDirectory = createMediaDirectory("/some/media");
        var mediaDirectory = createMediaDirectory("/some/media/dir1");

        parentDirectory.getDirectories().add(mediaDirectory);
        mediaDirectory.setParent(parentDirectory);

        var childDirectory1 = new MediaDirectory();
        childDirectory1.setPath("/some/media/dir1/dir2a");
        mediaDirectory.getDirectories().add(childDirectory1);
        childDirectory1.setParent(mediaDirectory);

        mediaDirectoryService.saveDirectoryStructure(parentDirectory);

        // ACT
        var result = mediaDirectoryService.getMediaDirectory("/some/media/dir1");

        // ASSERT
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(notNullValue()));
        assertThat(result.get().getFiles(), hasSize(0));
        assertThat(result.get().getParent(), is(notNullValue()));
        assertThat(result.get().getDirectories(), hasSize(1));
    }

    @Test
    void getMediaDirectory_childFilesNoDirsReturnsExpected() {
        // ARRANGE
        var parentDirectory = createMediaDirectory("/some/media");
        var mediaDirectory = createMediaDirectory("/some/media/dir1");
        var audioFile1 = createAudioFile(1);
        var audioFile2 = createAudioFile(2);

        audioFile2.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile2);
        audioFile1.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile1);

        parentDirectory.getDirectories().add(mediaDirectory);
        mediaDirectory.setParent(parentDirectory);

        mediaDirectoryService.saveDirectoryStructure(parentDirectory);

        // ACT
        var result = mediaDirectoryService.getMediaDirectory("/some/media/dir1");

        // ASSERT
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getFiles(), hasSize(2));
        assertThat(result.get().getFiles().get(1).getFileName(), is("2.mp3"));
        assertThat(result.get().getFiles().get(1).getId(), is(notNullValue()));
        assertThat(result.get().getParent(), is(notNullValue()));
        assertThat(result.get().getParent().getPath(), is("/some/media"));
    }

    @Test
    void getMediaDirectory_noParentReturnsExpected() {
        // ARRANGE
        var mediaDirectory = createMediaDirectory("/some/media/dir1");
        var audioFile1 = createAudioFile(1);
        var audioFile2 = createAudioFile(2);

        audioFile1.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile1);
        audioFile2.setParent(mediaDirectory);
        mediaDirectory.getFiles().add(audioFile2);

        mediaDirectoryService.saveDirectoryStructure(mediaDirectory);

        // ACT
        var result = mediaDirectoryService.getMediaDirectory("/some/media/dir1");

        // ASSERT
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getFiles(), hasSize(2));
        assertThat(result.get().getParent(), is(nullValue()));
    }
    
    @Test
    void saveDirectoryStructure_canSaveRealDirectoryStructure() {
        // ARRANGE
        Path currentRelativePath = Paths.get("");
        String projectRoot = currentRelativePath.toAbsolutePath().toString();
        var testPath = FileSystemUtils.joinPath(projectRoot, "src", "test", "resources", "testdata");

        // ACT
        var result = mediaDirectoryService.saveDirectoryStructure(testPath);

        // ASSERT
        assertThat(result, is(5));
    }
}
