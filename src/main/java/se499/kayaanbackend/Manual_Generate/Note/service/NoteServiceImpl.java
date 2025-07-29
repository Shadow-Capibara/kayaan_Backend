package se499.kayaanbackend.Manual_Generate.Note.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.entity.Note;
import se499.kayaanbackend.Manual_Generate.Note.repository.NoteRepository;
import se499.kayaanbackend.Manual_Generate.Group.entity.Group;
import se499.kayaanbackend.Manual_Generate.Group.repository.GroupRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final GroupRepository groupRepository;

    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, String username) {
        Note note = Note.builder()
                .content(dto.getContent())
                .subject(dto.getSubject())
                .difficulty(dto.getDifficulty())
                .tags(dto.getTags())
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
                        .imageUrl(n.getImageUrl())
                        .subject(n.getSubject())
                        .difficulty(n.getDifficulty())
                        .category(n.getCategory())
                        .tags(n.getTags())
                        .groupIds(n.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getNotesByCategory(String username, String category) {
        return noteRepository.findByCreatedByUsernameAndCategory(username, category)
                .stream()
                .map(n -> NoteResponseDTO.builder()
                        .id(n.getId())
                        .createdByUsername(n.getCreatedByUsername())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .imageUrl(n.getImageUrl())
                        .subject(n.getSubject())
                        .difficulty(n.getDifficulty())
                        .category(n.getCategory())
                        .tags(n.getTags())
                        .groupIds(n.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getNotesBySubject(String username, String subject) {
        return noteRepository.findByCreatedByUsernameAndSubject(username, subject)
                .stream()
                .map(n -> NoteResponseDTO.builder()
                        .id(n.getId())
                        .createdByUsername(n.getCreatedByUsername())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .imageUrl(n.getImageUrl())
                        .subject(n.getSubject())
                        .difficulty(n.getDifficulty())
                        .category(n.getCategory())
                        .tags(n.getTags())
                        .groupIds(n.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
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
                .imageUrl(note.getImageUrl())
                .subject(note.getSubject())
                .difficulty(note.getDifficulty())
                .category(note.getCategory())
                .tags(note.getTags())
                .groupIds(note.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
                .build();
    }

    @Override
    public NoteResponseDTO updateNote(Long id, NoteRequestDTO dto, String username) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found: " + id));
        if (!note.getCreatedByUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setImageUrl(dto.getImageUrl());
        note.setSubject(dto.getSubject());
        note.setDifficulty(dto.getDifficulty());
        note.setCategory(dto.getCategory());
        note.setTags(dto.getTags());
        List<Group> groups = dto.getGroupIds() == null ? java.util.Collections.emptyList() :
                groupRepository.findAllById(dto.getGroupIds());
        note.setSharedGroups(groups);
        Note saved = noteRepository.save(note);
        return NoteResponseDTO.builder()
                .id(saved.getId())
                .createdByUsername(saved.getCreatedByUsername())
                .title(saved.getTitle())
                .content(saved.getContent())
                .imageUrl(saved.getImageUrl())
                .subject(saved.getSubject())
                .difficulty(saved.getDifficulty())
                .category(saved.getCategory())
                .tags(saved.getTags())
                .groupIds(saved.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
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
