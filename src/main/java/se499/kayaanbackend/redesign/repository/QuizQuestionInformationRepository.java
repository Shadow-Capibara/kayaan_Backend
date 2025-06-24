package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.QuizQuestionInformation;

public interface QuizQuestionInformationRepository extends JpaRepository<QuizQuestionInformation, Integer> {
}
