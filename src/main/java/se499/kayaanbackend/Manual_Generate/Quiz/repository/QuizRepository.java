package se499.kayaanbackend.Manual_Generate.Quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.Quiz;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    @Query("SELECT q FROM Quiz q WHERE q.contentInfo.createdBy.username = :username")
    List<Quiz> findByCreatedByUsername(@Param("username") String username);
    
    @Query("SELECT q FROM Quiz q WHERE q.contentInfo.createdBy.username = :username AND q.contentInfo.subject = :subject")
    List<Quiz> findByCreatedByUsernameAndSubject(@Param("username") String username, @Param("subject") String subject);
    
    @Query("SELECT q FROM Quiz q WHERE q.contentInfo.contentId = :contentId")
    Optional<Quiz> findByContentInfo_ContentId(@Param("contentId") Long contentId);
}
