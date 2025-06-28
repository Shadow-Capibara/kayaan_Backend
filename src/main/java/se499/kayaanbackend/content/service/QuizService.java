package se499.kayaanbackend.content.service;

import se499.kayaanbackend.content.dto.QuizDto;

public interface QuizService {
    QuizDto createQuiz(Long userId, QuizDto dto);
    QuizDto getQuiz(Long id);
    QuizDto updateQuiz(Long id, QuizDto dto);
    void deleteQuiz(Long id);
    void restoreQuiz(Long id);
}
