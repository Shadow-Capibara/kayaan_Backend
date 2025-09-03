package se499.kayaanbackend.Manual_Generate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Manual_Generate.entity.ManualQuiz;

@Repository
public interface ManualQuizRepository extends JpaRepository<ManualQuiz, Long> {
    
    @Query("SELECT q FROM ManualQuiz q WHERE q.user.id = :userId AND q.deletedAt IS NULL ORDER BY q.createdAt DESC")
    List<ManualQuiz> findByUserIdAndNotDeleted(@Param("userId") Integer userId);
    
    @Query("SELECT q FROM ManualQuiz q WHERE q.id = :id AND q.user.id = :userId AND q.deletedAt IS NULL")
    Optional<ManualQuiz> findByIdAndUserIdAndNotDeleted(@Param("id") Long id, @Param("userId") Integer userId);
    
    @Query("SELECT q FROM ManualQuiz q WHERE q.user.username = :username AND q.deletedAt IS NULL ORDER BY q.createdAt DESC")
    List<ManualQuiz> findByUsernameAndNotDeleted(@Param("username") String username);
    
    @Query("SELECT q FROM ManualQuiz q WHERE q.id = :id AND q.user.username = :username AND q.deletedAt IS NULL")
    Optional<ManualQuiz> findByIdAndUsernameAndNotDeleted(@Param("id") Long id, @Param("username") String username);
    
    @Query("SELECT q FROM ManualQuiz q WHERE q.user.id = :userId AND q.subject = :subject AND q.deletedAt IS NULL ORDER BY q.createdAt DESC")
    List<ManualQuiz> findByUserIdAndSubjectAndNotDeleted(@Param("userId") Integer userId, @Param("subject") String subject);
    
    @Query("SELECT q FROM ManualQuiz q WHERE q.user.id = :userId AND q.difficulty = :difficulty AND q.deletedAt IS NULL ORDER BY q.createdAt DESC")
    List<ManualQuiz> findByUserIdAndDifficultyAndNotDeleted(@Param("userId") Integer userId, @Param("difficulty") String difficulty);
}
