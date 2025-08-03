package se499.kayaanbackend.Manual_Generate.contentInfo.service;

import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteRequestDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;

import java.util.List;

public interface ContentService {
    NoteResponseDTO createNote(NoteRequestDTO dto, String username);
    NoteResponseDTO updateNote(Long contentId, NoteRequestDTO dto, String username);
    void deleteNote(Long contentId, String username);
    NoteResponseDTO getNoteById(Long contentId, String username);
    List<NoteResponseDTO> getAllNotes(String username);
    List<NoteResponseDTO> getNotesBySubject(String username, String subject);

    // Quiz operations
    QuizResponseDTO createQuiz(QuizRequestDTO dto, String username);
    QuizResponseDTO updateQuiz(Long contentId, QuizRequestDTO dto, String username);
    void deleteQuiz(Long contentId, String username);
    QuizResponseDTO getQuizById(Long contentId, String username);
    List<QuizResponseDTO> getAllQuizzes(String username);
    List<QuizResponseDTO> getQuizzesBySubject(String username, String subject);

    // Flashcard operations
    FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username);
    FlashcardResponseDTO updateFlashcard(Long contentId, FlashcardRequestDTO dto, String username);
    void deleteFlashcard(Long contentId, String username);
    FlashcardResponseDTO getFlashcardById(Long contentId, String username);
    List<FlashcardResponseDTO> getAllFlashcards(String username);
    List<FlashcardResponseDTO> getFlashcardsBySubject(String username, String subject);
}
