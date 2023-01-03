package com.rogersillito.medialib.services;

import com.rogersillito.medialib.models.AudioFile;
import com.rogersillito.medialib.models.MediaDirectory;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class AudioFileFactoryImpl implements AudioFileFactory {
    @Override
    public AudioFile create(MediaDirectory parent, File file) throws RuntimeException {
        var audioFile = new AudioFile(parent, file.getName());
        org.jaudiotagger.audio.AudioFile jatAudioFile;
        try {
            jatAudioFile = AudioFileIO.read(file);
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException |
                 InvalidAudioFrameException e) {

            //TODO: handling of CannotReadVideoException (e.g. MP4)
            System.out.printf("Could not process Audio file: %s%n", file.getPath());
            throw new RuntimeException(e);
        }
        setMetadata(audioFile, jatAudioFile);
        return audioFile;
    }

    private Optional<String> getFirstOfField(Tag tag, FieldKey fieldKey) {
        if (tag.hasField(fieldKey)) {
            return Optional.of(tag.getFirst(fieldKey));
        }
        return Optional.empty();
    }

    private void setMetadata(AudioFile audioFile, org.jaudiotagger.audio.AudioFile jatAudioFile) {
        Tag tag = jatAudioFile.getTag();
//        for (var artist :
//                tag.getFields(FieldKey.ARTIST)) {
//            var artistContent = ((TagTextField)artist).getContent();
//            System.out.println(artistContent);
//        }
        getFirstOfField(tag, FieldKey.ARTIST).ifPresent(audioFile::setArtist);
        getFirstOfField(tag, FieldKey.ALBUM).ifPresent(audioFile::setAlbum);
        getFirstOfField(tag, FieldKey.ALBUM_ARTIST).ifPresent(audioFile::setAlbumArtist);
        getFirstOfField(tag, FieldKey.TITLE).ifPresent(audioFile::setTitle);
        getFirstOfField(tag, FieldKey.TRACK).ifPresent(audioFile::setTrack);

        AudioHeader header = jatAudioFile.getAudioHeader();
        audioFile.setType(header.getFormat());
    }
}
