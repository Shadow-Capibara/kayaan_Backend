package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.QuizQuestionInfo;

public interface QuizQuestionInfoRepository extends JpaRepository<QuizQuestionInfo, Integer> {
}
