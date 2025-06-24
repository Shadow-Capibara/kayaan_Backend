package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.Quiz;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCreatedByUsername(String username);
}
