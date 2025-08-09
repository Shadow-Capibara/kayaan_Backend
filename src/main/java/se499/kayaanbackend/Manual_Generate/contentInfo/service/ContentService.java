package se499.kayaanbackend.Manual_Generate.contentInfo.service;

import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;

import java.util.List;

public interface ContentService {
    
    // Centralized content creation methods
    NoteResponseDTO createNote(NoteRequestDTO dto, String username);
    FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username);
    QuizResponseDTO createQuiz(QuizRequestDTO dto, String username);
    
    // Update methods
    NoteResponseDTO updateNote(Long contentId, NoteRequestDTO dto, String username);
    FlashcardResponseDTO updateFlashcard(Long contentId, FlashcardRequestDTO dto, String username);
    QuizResponseDTO updateQuiz(Long contentId, QuizRequestDTO dto, String username);
    
    // Delete methods
    void deleteNote(Long contentId, String username);
    void deleteFlashcard(Long contentId, String username);
    void deleteQuiz(Long contentId, String username);
    
    // Get by ID methods
    NoteResponseDTO getNoteById(Long contentId, String username);
    FlashcardResponseDTO getFlashcardById(Long contentId, String username);
    QuizResponseDTO getQuizById(Long contentId, String username);
    
    // Get all methods
    List<NoteResponseDTO> getAllNotes(String username);
    List<FlashcardResponseDTO> getAllFlashcards(String username);
    List<QuizResponseDTO> getAllQuizzes(String username);
    
    // Get by subject methods
    List<NoteResponseDTO> getNotesBySubject(String username, String subject);
    List<FlashcardResponseDTO> getFlashcardsBySubject(String username, String subject);
    List<QuizResponseDTO> getQuizzesBySubject(String username, String subject);
}