package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.Flashcard;

import java.util.List;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByCreatedByUsername(String username);
}
