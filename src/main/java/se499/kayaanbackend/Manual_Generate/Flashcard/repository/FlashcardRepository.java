package se499.kayaanbackend.Manual_Generate.Flashcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Manual_Generate.Flashcard.entity.Flashcard;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    
    @Query("SELECT f FROM Flashcard f WHERE f.contentInfo.createdBy.username = :username")
    List<Flashcard> findByCreatedByUsername(@Param("username") String username);
    
    @Query("SELECT f FROM Flashcard f WHERE f.contentInfo.createdBy.username = :username AND f.contentInfo.subject = :subject")
    List<Flashcard> findByCreatedByUsernameAndSubject(@Param("username") String username, @Param("subject") String subject);
    
    @Query("SELECT f FROM Flashcard f WHERE f.contentInfo.contentId = :contentId")
    Optional<Flashcard> findByContentInfo_ContentId(@Param("contentId") Long contentId);
}
