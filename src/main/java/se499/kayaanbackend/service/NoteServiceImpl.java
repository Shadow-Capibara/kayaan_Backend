package se499.kayaanbackend.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.DTO.NoteRequestDTO;
import se499.kayaanbackend.DTO.NoteResponseDTO;
import se499.kayaanbackend.entity.ContentInformation;
import se499.kayaanbackend.entity.Note;
import se499.kayaanbackend.repository.ContentInformationRepository;
import se499.kayaanbackend.repository.NoteRepository;
import se499.kayaanbackend.security.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final ContentInformationRepository contentInformationRepository;
    private final UserService userService;

    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, String username) {
        var user = userService.findByUsername(username);

        ContentInformation content = ContentInformation.builder()
                .contentType(ContentInformation.ContentType.NOTE)
                .contentSubject(dto.getSubject())
                .contentTitle(dto.getTitle())
                .tag(dto.getTags() != null && !dto.getTags().isEmpty() ? dto.getTags().get(0) : null)
                .difficulty(ContentInformation.Difficulty.valueOf(dto.getDifficulty().toUpperCase()))
                .user(user)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        content = contentInformationRepository.save(content);

        Note note = Note.builder()
                .content(dto.getContent())
                .contentInformation(content)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        Note saved = noteRepository.save(note);
        return NoteResponseDTO.builder()
                .id(saved.getId())
                .createdByUsername(username)
                .title(content.getContentTitle())
                .content(saved.getContent())
                .subject(content.getContentSubject())
                .difficulty(content.getDifficulty().name())
                .tags(content.getTag() == null ? null : java.util.List.of(content.getTag()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getAllNotesForUser(String username) {
        return noteRepository.findByContentInformation_User_Username(username)
                .stream()
                .map(n -> NoteResponseDTO.builder()
                        .id(n.getId())
                        .createdByUsername(username)
                        .title(n.getContentInformation().getContentTitle())
                        .content(n.getContent())
                        .subject(n.getContentInformation().getContentSubject())
                        .difficulty(n.getContentInformation().getDifficulty().name())
                        .tags(n.getContentInformation().getTag() == null ? null : java.util.List.of(n.getContentInformation().getTag()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponseDTO getNoteById(Long id, String username) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found: " + id));
        if (!note.getContentInformation().getUser().getUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        return NoteResponseDTO.builder()
                .id(note.getId())
                .createdByUsername(username)
                .title(note.getContentInformation().getContentTitle())
                .content(note.getContent())
                .subject(note.getContentInformation().getContentSubject())
                .difficulty(note.getContentInformation().getDifficulty().name())
                .tags(note.getContentInformation().getTag() == null ? null : java.util.List.of(note.getContentInformation().getTag()))
                .build();
    }

    @Override
    public void deleteNote(Long id, String username) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found: " + id));
        if (!note.getContentInformation().getUser().getUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        noteRepository.delete(note);
    }
}
