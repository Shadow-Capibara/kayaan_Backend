package se499.kayaanbackend.content.service;

import se499.kayaanbackend.content.dto.NoteDto;

public interface NoteService {
    NoteDto createNote(Long userId, NoteDto dto);
    NoteDto getNote(Long id);
    NoteDto updateNote(Long id, NoteDto dto);
    void deleteNote(Long id);
    void restoreNote(Long id);
}
