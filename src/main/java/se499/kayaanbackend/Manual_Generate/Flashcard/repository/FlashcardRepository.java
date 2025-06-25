package se499.kayaanbackend.Manual_Generate.Flashcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.Flashcard.entity.Flashcard;

import java.util.List;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByCreatedByUsername(String username);
    List<Flashcard> findByCreatedByUsernameAndCategory(String username, String category);
    List<Flashcard> findByCreatedByUsernameAndSubject(String username, String subject);
}
