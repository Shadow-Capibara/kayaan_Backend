package se499.kayaanbackend.Manual_Generate.Quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.QuizQuestionChoice;

@Repository
public interface QuizQuestionChoiceRepository extends JpaRepository<QuizQuestionChoice, Long> {
}
