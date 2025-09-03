package se499.kayaanbackend.Manual_Generate.Note.service;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.entity.ManualNote;
import se499.kayaanbackend.Manual_Generate.repository.ManualNoteRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {
    
    private final ManualNoteRepository noteRepository;
    private final UserRepository userRepository;
    
    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, String username) {
        try {
            log.info("Creating note '{}' for user: {}", dto.getTitle(), username);
            
            // Find user
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            // Create note
            ManualNote note = ManualNote.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .subject(dto.getSubject())
                .difficulty(dto.getDifficulty())
                .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : null)
                .build();
                
            ManualNote savedNote = noteRepository.save(note);
            log.info("Note created with ID: {}", savedNote.getId());
            
            return mapToResponseDTO(savedNote);
            
        } catch (Exception e) {
            log.error("Error creating note for user: {}", username, e);
            throw new RuntimeException("Failed to create note: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDTO> getAllNotesForUser(String username) {
        try {
            log.info("Getting all notes for user: {}", username);
            
            List<ManualNote> notes = noteRepository.findByUsernameAndNotDeleted(username);
            
            return notes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting notes for user: {}", username, e);
            throw new RuntimeException("Failed to get notes: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public NoteResponseDTO getNoteById(Long id, String username) {
        try {
            log.info("Getting note {} for user: {}", id, username);
            
            ManualNote note = noteRepository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new RuntimeException("Note not found or access denied: " + id));
            
            return mapToResponseDTO(note);
            
        } catch (Exception e) {
            log.error("Error getting note {} for user: {}", id, username, e);
            throw new RuntimeException("Failed to get note: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteNote(Long id, String username) {
        try {
            log.info("Deleting note {} for user: {}", id, username);
            
            ManualNote note = noteRepository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new RuntimeException("Note not found or access denied: " + id));
            
            // Soft delete
            note.markAsDeleted();
            noteRepository.save(note);
            
            log.info("Note {} deleted successfully", id);
            
        } catch (Exception e) {
            log.error("Error deleting note {} for user: {}", id, username, e);
            throw new RuntimeException("Failed to delete note: " + e.getMessage());
        }
    }
    
    // Additional method for search functionality
    public List<NoteResponseDTO> searchNotes(String username, String searchTerm) {
        try {
            log.info("Searching notes for user: {} with term: {}", username, searchTerm);
            
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            List<ManualNote> notes = noteRepository.searchByUserIdAndContent(user.getId(), searchTerm);
            
            return notes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error searching notes for user: {}", username, e);
            throw new RuntimeException("Failed to search notes: " + e.getMessage());
        }
    }
    
    private NoteResponseDTO mapToResponseDTO(ManualNote note) {
        return NoteResponseDTO.builder()
            .id(note.getId())
            .createdByUsername(note.getUser().getUsername())
            .title(note.getTitle())
            .content(note.getContent())
            .subject(note.getSubject())
            .difficulty(note.getDifficulty())
            .tags(note.getTags() != null && !note.getTags().trim().isEmpty() 
                ? Arrays.asList(note.getTags().split(","))
                : List.of())
            .createdAt(note.getCreatedAt())
            .updatedAt(note.getUpdatedAt())
            .build();
    }
}
