package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.QuizInfo;

public interface QuizInfoRepository extends JpaRepository<QuizInfo, Integer> {
}
