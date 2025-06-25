package se499.kayaanbackend.Manual_Generate.Note.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/api/manual/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(
            @RequestBody NoteRequestDTO dto
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        NoteResponseDTO saved = noteService.createNote(dto, username);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getAllNotes() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(noteService.getAllNotesForUser(username));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<NoteResponseDTO>> filterNotes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subject
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (category != null) {
            return ResponseEntity.ok(noteService.getNotesByCategory(username, category));
        }
        if (subject != null) {
            return ResponseEntity.ok(noteService.getNotesBySubject(username, subject));
        }
        return ResponseEntity.ok(noteService.getAllNotesForUser(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        NoteResponseDTO dto = noteService.getNoteById(id, username);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        noteService.deleteNote(id, username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long id,
                                                      @RequestBody NoteRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        NoteResponseDTO updated = noteService.updateNote(id, dto, username);
        return ResponseEntity.ok(updated);
    }
}
