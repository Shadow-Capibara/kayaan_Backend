package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.QuizQuestion;

public class QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
}
