package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

}
