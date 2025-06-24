package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.QuizQuestionChoice;

public interface QuizQuestionChoiceRepository extends JpaRepository<QuizQuestionChoice, Integer> {
}
