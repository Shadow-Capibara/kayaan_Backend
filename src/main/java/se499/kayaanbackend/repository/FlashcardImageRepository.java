package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.FlashcardImage;

public interface FlashcardImageRepository extends JpaRepository<FlashcardImage, Long> {
}
