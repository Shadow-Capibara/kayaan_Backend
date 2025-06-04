package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.Quiz;

import java.util.List;

public class QuizRepository extends JpaRepository<Quiz, Integer> {
    // Fetch all quizzes created by a particular user
    List<Quiz> findByCreatedByUsername(String username);
}
