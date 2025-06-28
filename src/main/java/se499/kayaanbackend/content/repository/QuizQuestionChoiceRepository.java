package se499.kayaanbackend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.content.entity.QuizQuestionChoice;

public interface QuizQuestionChoiceRepository extends JpaRepository<QuizQuestionChoice, Long> {
}
