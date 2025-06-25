package se499.kayaanbackend.Manual_Generate.Quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.Quiz;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCreatedByUsername(String username);
    List<Quiz> findByCreatedByUsernameAndCategory(String username, String category);
    List<Quiz> findByCreatedByUsernameAndQuestions_Subject(String username, String subject);
}
