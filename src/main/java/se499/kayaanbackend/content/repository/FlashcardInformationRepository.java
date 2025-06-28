package se499.kayaanbackend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.content.entity.FlashcardInformation;

public interface FlashcardInformationRepository extends JpaRepository<FlashcardInformation, Long> {
}
