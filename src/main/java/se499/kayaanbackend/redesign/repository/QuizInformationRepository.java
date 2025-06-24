package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.QuizInformation;

public interface QuizInformationRepository extends JpaRepository<QuizInformation, Integer> {
}
