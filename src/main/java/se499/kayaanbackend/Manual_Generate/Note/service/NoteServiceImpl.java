package se499.kayaanbackend.Manual_Generate.Note.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.repository.NoteRepository;

@Service
public class NoteServiceImpl implements NoteService {
    
    private final NoteRepository noteRepository;
    
    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }
    
    @Override
    public NoteResponseDTO createNote(NoteRequestDTO dto, String username) {
        // Stub implementation - return null for now
        return null;
    }
    
    @Override
    public List<NoteResponseDTO> getAllNotesForUser(String username) {
        // Stub implementation - return empty list
        return List.of();
    }
    
    @Override
    public NoteResponseDTO getNoteById(Long id, String username) {
        // Stub implementation - return null for now
        return null;
    }
    
    @Override
    public void deleteNote(Long id, String username) {
        // Stub implementation - do nothing
    }
}
