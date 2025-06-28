package se499.kayaanbackend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.content.entity.FlashcardImage;

public interface FlashcardImageRepository extends JpaRepository<FlashcardImage, Long> {
}
