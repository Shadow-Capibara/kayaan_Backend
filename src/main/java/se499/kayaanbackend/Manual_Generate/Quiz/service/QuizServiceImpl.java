package se499.kayaanbackend.Manual_Generate.Quiz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizRequestDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.repository.QuizRepository;

@Service
public class QuizServiceImpl implements QuizService {
    
    private final QuizRepository quizRepository;
    
    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }
    
    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDto, String username) {
        // Stub implementation - return null for now
        return null;
    }
    
    @Override
    public QuizResponseDTO getQuizById(Long id, String username) {
        // Stub implementation - return null for now
        return null;
    }
    
    @Override
    public List<QuizResponseDTO> getAllQuizzesForUser(String username) {
        // Stub implementation - return empty list
        return List.of();
    }
    
    @Override
    public void deleteQuiz(Long id, String username) {
        // Stub implementation - do nothing
    }
}
