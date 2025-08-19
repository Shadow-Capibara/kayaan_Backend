package se499.kayaanbackend.Manual_Generate.Flashcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    Optional<Flashcard> findByContent_ContentId(Long contentId);

    @Query("SELECT f FROM Flashcard f JOIN f.content c WHERE c.userCreatedAt.username = :username")
    List<Flashcard> findAllByUsername(@Param("username") String username);

//    List<Flashcard> findByCreatedByUsername(String username);
//    List<Flashcard> findByCreatedByUsernameAndCategory(String username, String category);
//    List<Flashcard> findByCreatedByUsernameAndSubject(String username, String subject);
}
