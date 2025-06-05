package se499.kayaanbackend.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.DTO.NoteRequestDTO;
import se499.kayaanbackend.DTO.NoteResponseDTO;
import se499.kayaanbackend.entity.Note;
import se499.kayaanbackend.repository.NoteRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;

    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, String username) {
        Note note = Note.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .subject(dto.getSubject())
                .difficulty(dto.getDifficulty())
                .tags(dto.getTags())
                .createdByUsername(username)
                .build();

        Note saved = noteRepository.save(note);
        return NoteResponseDTO.builder()
                .id(saved.getId())
                .createdByUsername(saved.getCreatedByUsername())
                .title(saved.getTitle())
                .content(saved.getContent())
                .subject(saved.getSubject())
                .difficulty(saved.getDifficulty())
                .tags(saved.getTags())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getAllNotesForUser(String username) {
        return noteRepository.findByCreatedByUsername(username)
                .stream()
                .map(n -> NoteResponseDTO.builder()
                        .id(n.getId())
                        .createdByUsername(n.getCreatedByUsername())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .subject(n.getSubject())
                        .difficulty(n.getDifficulty())
                        .tags(n.getTags())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponseDTO getNoteById(Long id, String username) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found: " + id));
        if (!note.getCreatedByUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        return NoteResponseDTO.builder()
                .id(note.getId())
                .createdByUsername(note.getCreatedByUsername())
                .title(note.getTitle())
                .content(note.getContent())
                .subject(note.getSubject())
                .difficulty(note.getDifficulty())
                .tags(note.getTags())
                .build();
    }

    @Override
    public void deleteNote(Long id, String username) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found: " + id));
        if (!note.getCreatedByUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        noteRepository.delete(note);
    }
}
