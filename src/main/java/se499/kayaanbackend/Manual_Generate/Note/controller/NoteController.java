package se499.kayaanbackend.Manual_Generate.Note.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.service.NoteService;
import se499.kayaanbackend.security.user.User;

@RestController
@RequestMapping("/api/note")
@Slf4j
public class NoteController {
    
    private final NoteService noteService;
    
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    
    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(
            @RequestBody NoteRequestDTO request,
            @AuthenticationPrincipal User currentUser) {
        try {
            // Detailed logging for debugging
            log.info("=== Note Creation Request ===");
            log.info("Request: {}", request);
            log.info("CurrentUser: {}", currentUser);
            
            if (currentUser == null) {
                log.error("Authentication failed - currentUser is null");
                throw new RuntimeException("User authentication failed - currentUser is null");
            }
            
            log.info("Creating note for user: {}", currentUser.getUsername());
            NoteResponseDTO savedNote = noteService.createNote(request, currentUser.getUsername());
            log.info("Note created successfully with ID: {}", savedNote.getId());
            
            return ResponseEntity.ok(savedNote);
        } catch (Exception e) {
            log.error("Error creating note: ", e);
            throw new RuntimeException("Error creating note: " + e.getMessage(), e);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getAllNotes(
            @AuthenticationPrincipal User currentUser) {
        List<NoteResponseDTO> notes = noteService.getAllNotesForUser(currentUser.getUsername());
        return ResponseEntity.ok(notes);
    }

    // Frontend compatible endpoint
    @GetMapping("/user/{username}")
    public ResponseEntity<List<NoteResponseDTO>> getAllNotesForUserByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser) {
        
        // Security check - only allow users to access their own notes
        if (!currentUser.getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        List<NoteResponseDTO> notes = noteService.getAllNotesForUser(username);
        return ResponseEntity.ok(notes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        NoteResponseDTO note = noteService.getNoteById(id, currentUser.getUsername());
        return ResponseEntity.ok(note);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        noteService.deleteNote(id, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }
}
