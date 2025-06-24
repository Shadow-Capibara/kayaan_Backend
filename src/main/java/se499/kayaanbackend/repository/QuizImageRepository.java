package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.QuizImage;

public interface QuizImageRepository extends JpaRepository<QuizImage, Long> {
}
