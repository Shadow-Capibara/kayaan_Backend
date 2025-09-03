package se499.kayaanbackend.Manual_Generate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Manual_Generate.entity.ManualFlashcard;

@Repository
public interface ManualFlashcardRepository extends JpaRepository<ManualFlashcard, Long> {
    
    @Query("SELECT f FROM ManualFlashcard f WHERE f.user.id = :userId AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<ManualFlashcard> findByUserIdAndNotDeleted(@Param("userId") Integer userId);
    
    @Query("SELECT f FROM ManualFlashcard f WHERE f.id = :id AND f.user.id = :userId AND f.deletedAt IS NULL")
    Optional<ManualFlashcard> findByIdAndUserIdAndNotDeleted(@Param("id") Long id, @Param("userId") Integer userId);
    
    @Query("SELECT f FROM ManualFlashcard f WHERE f.user.username = :username AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<ManualFlashcard> findByUsernameAndNotDeleted(@Param("username") String username);
    
    @Query("SELECT f FROM ManualFlashcard f WHERE f.id = :id AND f.user.username = :username AND f.deletedAt IS NULL")
    Optional<ManualFlashcard> findByIdAndUsernameAndNotDeleted(@Param("id") Long id, @Param("username") String username);
    
    @Query("SELECT f FROM ManualFlashcard f WHERE f.user.id = :userId AND f.subject = :subject AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<ManualFlashcard> findByUserIdAndSubjectAndNotDeleted(@Param("userId") Integer userId, @Param("subject") String subject);
    
    @Query("SELECT f FROM ManualFlashcard f WHERE f.user.id = :userId AND f.difficulty = :difficulty AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<ManualFlashcard> findByUserIdAndDifficultyAndNotDeleted(@Param("userId") Integer userId, @Param("difficulty") String difficulty);
}
