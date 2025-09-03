package se499.kayaanbackend.Manual_Generate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Manual_Generate.entity.ManualNote;

@Repository
public interface ManualNoteRepository extends JpaRepository<ManualNote, Long> {
    
    @Query("SELECT n FROM ManualNote n WHERE n.user.id = :userId AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<ManualNote> findByUserIdAndNotDeleted(@Param("userId") Integer userId);
    
    @Query("SELECT n FROM ManualNote n WHERE n.id = :id AND n.user.id = :userId AND n.deletedAt IS NULL")
    Optional<ManualNote> findByIdAndUserIdAndNotDeleted(@Param("id") Long id, @Param("userId") Integer userId);
    
    @Query("SELECT n FROM ManualNote n WHERE n.user.username = :username AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<ManualNote> findByUsernameAndNotDeleted(@Param("username") String username);
    
    @Query("SELECT n FROM ManualNote n WHERE n.id = :id AND n.user.username = :username AND n.deletedAt IS NULL")
    Optional<ManualNote> findByIdAndUsernameAndNotDeleted(@Param("id") Long id, @Param("username") String username);
    
    @Query("SELECT n FROM ManualNote n WHERE n.user.id = :userId AND n.subject = :subject AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<ManualNote> findByUserIdAndSubjectAndNotDeleted(@Param("userId") Integer userId, @Param("subject") String subject);
    
    @Query("SELECT n FROM ManualNote n WHERE n.user.id = :userId AND n.difficulty = :difficulty AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<ManualNote> findByUserIdAndDifficultyAndNotDeleted(@Param("userId") Integer userId, @Param("difficulty") String difficulty);
    
    @Query("SELECT n FROM ManualNote n WHERE n.user.id = :userId AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<ManualNote> searchByUserIdAndContent(@Param("userId") Integer userId, @Param("searchTerm") String searchTerm);
}
