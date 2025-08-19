package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.Flashcard.entity.FlashcardImage;

public interface FlashcardImgRepository extends JpaRepository<FlashcardImage, Integer> {
}
