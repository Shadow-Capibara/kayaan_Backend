package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.QuizImage;

public interface QuizImageRepository extends JpaRepository<QuizImage, Integer> {
}
