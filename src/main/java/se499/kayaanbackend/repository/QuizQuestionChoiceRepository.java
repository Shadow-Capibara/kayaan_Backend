package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.QuizQuestionChoice;

public interface QuizQuestionChoiceRepository extends JpaRepository<QuizQuestionChoice, Long> {
}
