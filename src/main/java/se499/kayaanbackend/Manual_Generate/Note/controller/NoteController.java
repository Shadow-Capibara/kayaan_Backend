package se499.kayaanbackend.Manual_Generate.Note.controller;

import java.util.List;

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
@RequestMapping("/api/notes")
public class NoteController {
    
    private final NoteService noteService;
    
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    
    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(
            @RequestBody NoteRequestDTO request,
            @AuthenticationPrincipal User currentUser) {
        // Stub implementation - return null for now
        return ResponseEntity.ok(null);
    }
    
    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getAllNotes(
            @AuthenticationPrincipal User currentUser) {
        // Stub implementation - return empty list
        return ResponseEntity.ok(List.of());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        // Stub implementation - return null for now
        return ResponseEntity.ok(null);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        // Stub implementation - do nothing
        return ResponseEntity.ok().build();
    }
}
