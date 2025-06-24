package se499.kayaanbackend.Manual_Generate.Note.service;

import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;

import java.util.List;

public interface NoteService {
    NoteResponseDTO createNote(NoteRequestDTO dto, String username);
    List<NoteResponseDTO> getAllNotesForUser(String username);
    NoteResponseDTO getNoteById(Long id, String username);
    void deleteNote(Long id, String username);
}
