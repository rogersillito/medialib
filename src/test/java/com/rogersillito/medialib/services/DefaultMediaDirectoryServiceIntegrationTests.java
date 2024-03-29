package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.repositories.AudioFileRepository;
import com.rogersillito.medialib.repositories.MediaDirectoryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Tag("Integration")
public class DefaultMediaDirectoryServiceIntegrationTests {

    @Autowired
    private MediaDirectoryService mediaDirectoryService;
    @Autowired
    private MediaDirectoryRepository mediaDirectoryRepository;
    @Autowired
    private AudioFileRepository audioFileRepository;

    private final List<MediaDirectory> testDirectories = new ArrayList<>();
    private static final String testDataPath = getTestDataPath();

    private static String getTestDataPath() {
        Path currentRelativePath = Paths.get("");
        String projectRoot = currentRelativePath.toAbsolutePath().toString();
        return FileSystemUtils.joinPath(projectRoot, "src", "test", "resources", "testdata");
    }

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
        this.mediaDirectoryRepository.findAll().stream()
                .filter(md -> md.getPath().startsWith(testDataPath))
                .forEach(this.mediaDirectoryRepository::delete);
    }

    @Test
    void getMediaDirectory_childDirsAndFilesReturnsExpected() {
        // ARRANGE
        var parentDirectory = createMediaDirectory("/some/media");
        var mediaDirectory = createMediaDirectory("/some/media/dir1");
        var audioFile1 = createAudioFile(1);
        var audioFile2 = createAudioFile(2);

        mediaDirectory.addFile(audioFile2);
        mediaDirectory.addFile(audioFile1);

        parentDirectory.addSubdirectory(mediaDirectory);

        var childDirectory2 = createMediaDirectory("/some/media/dir1/dir2b");
        mediaDirectory.addSubdirectory(childDirectory2);
        var childDirectory1 = createMediaDirectory("/some/media/dir1/dir2a");
        mediaDirectory.addSubdirectory(childDirectory1);

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

        parentDirectory.addSubdirectory(mediaDirectory);

        var childDirectory1 = createMediaDirectory("/some/media/dir1/dir2a");
        mediaDirectory.addSubdirectory(childDirectory1);

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

        mediaDirectory.addFile(audioFile2);
        mediaDirectory.addFile(audioFile1);

        parentDirectory.addSubdirectory(mediaDirectory);

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

        mediaDirectory.addFile(audioFile1);
        mediaDirectory.addFile(audioFile2);

        mediaDirectoryService.saveDirectoryStructure(mediaDirectory);

        // ACT
        var result = mediaDirectoryService.getMediaDirectory("/some/media/dir1");

        // ASSERT
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getFiles(), hasSize(2));
        assertThat(result.get().getParent(), is(nullValue()));
    }

    @Test
    @Transactional
    void deleteDirectoryStructure_canDeleteIntermediateDirectoryAndUpdateParent() {
        // ARRANGE
        var parentDirectory = createMediaDirectory("/some/media");
        var parentFile1 = createAudioFile(0);
        parentDirectory.addFile(parentFile1);

        var mediaDirectory = createMediaDirectory("/some/media/dir1");

        var audioFile1 = createAudioFile(1);
        mediaDirectory.addFile(audioFile1);

        parentDirectory.addSubdirectory(mediaDirectory);

        var childDirectory1 = createMediaDirectory("/some/media/dir1/dir2a");
        mediaDirectory.addSubdirectory(childDirectory1);

        var audioFile2 = createAudioFile(2);
        childDirectory1.addFile(audioFile2);

        mediaDirectoryService.saveDirectoryStructure(parentDirectory);

        // ACT
        mediaDirectoryService.deleteDirectoryStructure("/some/media/dir1");

        // ASSERT
        var deletedDir = mediaDirectoryRepository.findByPath("/some/media/dir1");
        assertThat(deletedDir, is(nullValue()));

        var testMediaDirs = testDirectories.stream()
                .map(MediaDirectory::getPath)
                .map(mediaDirectoryRepository::findByPath)
                .filter(Objects::nonNull)
                .toList();
        assertThat(testMediaDirs, hasSize(1));
        assertThat(testMediaDirs.get(0).getPath(), is("/some/media"));

        var audioFiles = audioFileRepository.findAll();
        assertThat(audioFiles, hasSize(1));
        assertThat(audioFiles.get(0).getFileName(), is("0.mp3"));
        assertThat(audioFiles.get(0).getParent().getDirectories(), hasSize(0));
    }

    @Test
    void saveDirectoryStructure_canSaveAndOverwriteUsingRealDirectoryStructure() {
        // ACT
        mediaDirectoryService.saveDirectoryStructure(testDataPath);
        mediaDirectoryService.saveDirectoryStructure(testDataPath);

        // ASSERT
        var directoryCount = this.mediaDirectoryRepository.findAll().stream()
                .filter(md -> md.getPath().startsWith(testDataPath))
                .count();
        assertThat(directoryCount, is(4L));
    }

    @Test
    @Transactional
    void saveDirectoryStructure_saveDirFromRealDirectoryStructureIsLinkedWithAlreadyPersistedParent() {
        // ARRANGE
        var parentDirectory = createMediaDirectory(testDataPath);
        parentDirectory.addFile(createAudioFile(991));
        parentDirectory.addFile(createAudioFile(992));

        var subDirectory1 = createMediaDirectory(FileSystemUtils.joinPath(testDataPath, "A"));
        parentDirectory.addSubdirectory(subDirectory1);
        subDirectory1.addFile(createAudioFile(993));
        subDirectory1.addFile(createAudioFile(994));

        var subDirectory2Path = FileSystemUtils.joinPath(testDataPath, "A", "B");

        // save "already persisted" files/dirs
        mediaDirectoryService.saveDirectoryStructure(parentDirectory);

        // ACT
        mediaDirectoryService.saveDirectoryStructure(subDirectory2Path);

        // ASSERT
        var directories = this.mediaDirectoryRepository.findAll().stream()
                .filter(dir -> dir.getPath().startsWith(testDataPath))
                .toList();
        var files = this.audioFileRepository.findAll().stream()
                .map(AudioFile::getFilePath)
                .filter(path -> path.startsWith(testDataPath))
                .toList();
        assertThat(directories, hasSize(4));
        assertThat(files, hasSize(6));
        var subDir2List = directories.stream()
                .filter(d -> d.getPath().equals(subDirectory2Path))
                .toList();
        assertThat(subDir2List, hasSize(1));
        var subDir2Parent = subDir2List.get(0).getParent();
        assertThat(subDir2Parent, is(notNullValue()));
        assertThat(subDir2Parent.getPath(), is(subDirectory1.getPath()));
    }
}
