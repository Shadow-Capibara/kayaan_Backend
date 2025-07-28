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
    NoteResponseDTO updateNote(Integer contentId, NoteRequestDTO dto, String username);
    void deleteNote(Integer contentId, String username);
    NoteResponseDTO getNoteById(Integer contentId, String username);
    List<NoteResponseDTO> getAllNotes(String username);
    List<NoteResponseDTO> getNotesBySubject(String username, String subject);

    // Quiz operations
    QuizResponseDTO createQuiz(QuizRequestDTO dto, String username);
    QuizResponseDTO updateQuiz(Integer contentId, QuizRequestDTO dto, String username);
    void deleteQuiz(Integer contentId, String username);
    QuizResponseDTO getQuizById(Integer contentId, String username);
    List<QuizResponseDTO> getAllQuizzes(String username);
    List<QuizResponseDTO> getQuizzesBySubject(String username, String subject);

    // Flashcard operations
    FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username);
    FlashcardResponseDTO updateFlashcard(Integer contentId, FlashcardRequestDTO dto, String username);
    void deleteFlashcard(Integer contentId, String username);
    FlashcardResponseDTO getFlashcardById(Integer contentId, String username);
    List<FlashcardResponseDTO> getAllFlashcards(String username);
    List<FlashcardResponseDTO> getFlashcardsBySubject(String username, String subject);
}
