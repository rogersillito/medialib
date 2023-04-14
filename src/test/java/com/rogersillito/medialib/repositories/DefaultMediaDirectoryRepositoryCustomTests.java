package com.rogersillito.medialib.repositories;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import com.rogersillito.medialib.services.MediaDirectoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "command.line.runner.enabled=false" })
public class DefaultMediaDirectoryRepositoryCustomTests {

    @Autowired
    MediaDirectoryRepository mediaDirectoryRepository;

    @Autowired
    MediaDirectoryService mediaDirectoryService;

    void setupData() {
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

        mediaDirectoryService.saveDirectoryStructure(parentDirectory);
    }

    AudioFile audioFile(Integer title) {
        AudioFile audioFile = new AudioFile();
        audioFile.setTitle(title.toString());
        audioFile.setFileName(title.toString().concat(".mp3"));
        return audioFile;
    }

    @Test
    void DefaultMediaDirectoryRepositoryCustom_findMediaDirectoryClientResponseByPath_returnsExpected() {
        setupData();
        var result = mediaDirectoryRepository.findMediaDirectoryClientResponseByPath("/some/media/dir1");

//        repository.saveCustomers(repository);
//        assertThat(null, hasSize(5));
    }
}
