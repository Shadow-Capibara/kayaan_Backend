package se499.kayaanbackend.Manual_Generate.Quiz.service;

import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;

import java.util.List;

public interface QuizService {
    // Create a new quiz for the given username
    QuizResponseDTO createQuiz(QuizRequestDTO requestDto, String createdByUsername);

    // Fetch all quizzes this user created
    List<QuizResponseDTO> getAllQuizzesForUser(String username);
    List<QuizResponseDTO> getQuizzesByCategory(String username, String category);
    List<QuizResponseDTO> getQuizzesBySubject(String username, String subject);

    // Fetch a single quiz by id (only if it belongs to this user)
    QuizResponseDTO getQuizById(Long quizId, String username);

    QuizResponseDTO updateQuiz(Long quizId, QuizRequestDTO requestDto, String username);

    // Delete a quiz (if owned by this user)
    void deleteQuiz(Long quizId, String username);
}
