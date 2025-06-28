package se499.kayaanbackend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.content.entity.QuizQuestionInformation;

public interface QuizQuestionInformationRepository extends JpaRepository<QuizQuestionInformation, Long> {
}
