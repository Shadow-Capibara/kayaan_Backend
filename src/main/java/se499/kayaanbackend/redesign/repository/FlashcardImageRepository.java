package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.FlashcardImage;

public interface FlashcardImageRepository extends JpaRepository<FlashcardImage, Integer> {
}
