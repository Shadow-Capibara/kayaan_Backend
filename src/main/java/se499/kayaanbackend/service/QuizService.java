package se499.kayaanbackend.service;

import se499.kayaanbackend.DTO.QuizRequestDTO;
import se499.kayaanbackend.DTO.QuizResponseDTO;

import java.util.List;

public interface QuizService {
    // Create a new quiz for the given username
    QuizResponseDTO createQuiz(QuizRequestDTO requestDto, String createdByUsername);

    // Fetch all quizzes this user created
    List<QuizResponseDTO> getAllQuizzesForUser(String username);

    // Fetch a single quiz by id (only if it belongs to this user)
    QuizResponseDTO getQuizById(Long quizId, String username);

    // Delete a quiz (if owned by this user)
    void deleteQuiz(Long quizId, String username);
}
