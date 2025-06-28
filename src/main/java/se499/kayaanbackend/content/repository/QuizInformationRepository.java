package se499.kayaanbackend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.content.entity.QuizInformation;

public interface QuizInformationRepository extends JpaRepository<QuizInformation, Long> {
}
