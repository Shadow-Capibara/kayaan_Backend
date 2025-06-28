package se499.kayaanbackend.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.content.dto.NoteDto;
import se499.kayaanbackend.content.dto.NoteImageDto;
import se499.kayaanbackend.content.entity.ContentInformation;
import se499.kayaanbackend.content.entity.NoteImage;
import se499.kayaanbackend.content.entity.NoteInformation;
import se499.kayaanbackend.content.enums.ContentType;
import se499.kayaanbackend.content.repository.ContentInformationRepository;
import se499.kayaanbackend.content.repository.NoteInformationRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {

    private final ContentInformationRepository contentRepository;
    private final NoteInformationRepository noteRepository;

    @Override
    public NoteDto createNote(Long userId, NoteDto dto) {
        ContentInformation content = ContentInformation.builder()
                .userId(userId)
                .contentType(ContentType.NOTE)
                .build();
        contentRepository.save(content);

        NoteInformation note = NoteInformation.builder()
                .contentInformation(content)
                .noteTitle(dto.getNoteTitle())
                .build();

        if (dto.getImages() != null) {
            note.setImages(dto.getImages().stream()
                    .map(i -> NoteImage.builder()
                            .note(note)
                            .imageUrl(i.getImageUrl())
                            .build())
                    .collect(Collectors.toList()));
        }

        NoteInformation saved = noteRepository.save(note);
        dto.setNoteId(saved.getNoteId());
        dto.setContentId(content.getContentId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public NoteDto getNote(Long id) {
        NoteInformation note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        return toDto(note);
    }

    @Override
    public NoteDto updateNote(Long id, NoteDto dto) {
        NoteInformation note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setNoteTitle(dto.getNoteTitle());
        note.getImages().clear();
        if (dto.getImages() != null) {
            note.getImages().addAll(dto.getImages().stream()
                    .map(i -> NoteImage.builder()
                            .note(note)
                            .imageUrl(i.getImageUrl())
                            .build())
                    .collect(Collectors.toList()));
        }
        return toDto(noteRepository.save(note));
    }

    @Override
    public void deleteNote(Long id) {
        NoteInformation note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        LocalDateTime now = LocalDateTime.now();
        note.setDeletedAt(now);
        note.getContentInformation().setDeletedAt(now);
        noteRepository.save(note);
    }

    @Override
    public void restoreNote(Long id) {
        NoteInformation note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setDeletedAt(null);
        note.getContentInformation().setDeletedAt(null);
        noteRepository.save(note);
    }

    private NoteDto toDto(NoteInformation note) {
        NoteDto dto = new NoteDto();
        dto.setNoteId(note.getNoteId());
        dto.setContentId(note.getContentInformation().getContentId());
        dto.setNoteTitle(note.getNoteTitle());
        dto.setImages(
                note.getImages().stream()
                        .map(i -> {
                            NoteImageDto idto = new NoteImageDto();
                            idto.setImageId(i.getImageId());
                            idto.setImageUrl(i.getImageUrl());
                            return idto;
                        }).collect(Collectors.toList())
        );
        return dto;
    }
}
