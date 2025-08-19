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



//    private final NoteService noteService;
//
//    @PostMapping
//    public ResponseEntity<NoteResponseDTO> createNote(
//            @RequestBody NoteRequestDTO dto
//    ) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        NoteResponseDTO saved = noteService.createNote(dto, username);
//        return ResponseEntity.ok(saved);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<NoteResponseDTO>> getAllNotes() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return ResponseEntity.ok(noteService.getAllNotesForUser(username));
//    }
//
//    @GetMapping("/filter")
//    public ResponseEntity<List<NoteResponseDTO>> filterNotes(
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false) String subject
//    ) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (category != null) {
//            return ResponseEntity.ok(noteService.getNotesByCategory(username, category));
//        }
//        if (subject != null) {
//            return ResponseEntity.ok(noteService.getNotesBySubject(username, subject));
//        }
//        return ResponseEntity.ok(noteService.getAllNotesForUser(username));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        NoteResponseDTO dto = noteService.getNoteById(id, username);
//        return ResponseEntity.ok(dto);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        noteService.deleteNote(id, username);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long id,
//                                                      @RequestBody NoteRequestDTO dto) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        NoteResponseDTO updated = noteService.updateNote(id, dto, username);
//        return ResponseEntity.ok(updated);
//    }
}
