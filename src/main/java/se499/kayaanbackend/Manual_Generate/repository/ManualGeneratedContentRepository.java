package se499.kayaanbackend.Manual_Generate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.AI_Generate.entity.ContentType;
import se499.kayaanbackend.Manual_Generate.entity.ManualGeneratedContent;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ManualGeneratedContent entity
 * Provides methods similar to AIGeneratedContentRepository for consistency
 */
@Repository
public interface ManualGeneratedContentRepository extends JpaRepository<ManualGeneratedContent, Long> {
    
    // Find by user and not deleted
    @Query("SELECT mgc FROM ManualGeneratedContent mgc " +
           "WHERE mgc.user.username = :username " +
           "AND mgc.deletedAt IS NULL " +
           "ORDER BY mgc.createdAt DESC")
    List<ManualGeneratedContent> findByUsernameAndNotDeleted(@Param("username") String username);
    
    // Find by user, content type and not deleted
    @Query("SELECT mgc FROM ManualGeneratedContent mgc " +
           "WHERE mgc.user.username = :username " +
           "AND mgc.contentType = :contentType " +
           "AND mgc.deletedAt IS NULL " +
           "ORDER BY mgc.createdAt DESC")
    List<ManualGeneratedContent> findByUsernameAndContentTypeAndNotDeleted(
        @Param("username") String username, 
        @Param("contentType") ContentType contentType
    );
    
    // Find by ID and user (for access control)
    @Query("SELECT mgc FROM ManualGeneratedContent mgc " +
           "WHERE mgc.id = :id " +
           "AND mgc.user.username = :username " +
           "AND mgc.deletedAt IS NULL")
    Optional<ManualGeneratedContent> findByIdAndUsernameAndNotDeleted(
        @Param("id") Long id, 
        @Param("username") String username
    );
    
    // Paginated search with filters
    @Query("SELECT mgc FROM ManualGeneratedContent mgc " +
           "WHERE mgc.user.username = :username " +
           "AND mgc.deletedAt IS NULL " +
           "AND (:contentType IS NULL OR mgc.contentType = :contentType) " +
           "AND (:subject IS NULL OR mgc.subject LIKE %:subject%) " +
           "AND (:difficulty IS NULL OR mgc.difficulty = :difficulty)")
    Page<ManualGeneratedContent> findByFilters(
        @Param("username") String username,
        @Param("contentType") ContentType contentType,
        @Param("subject") String subject,
        @Param("difficulty") String difficulty,
        Pageable pageable
    );
    
    // Count by user and content type
    @Query("SELECT COUNT(mgc) FROM ManualGeneratedContent mgc " +
           "WHERE mgc.user.username = :username " +
           "AND mgc.contentType = :contentType " +
           "AND mgc.deletedAt IS NULL")
    long countByUsernameAndContentTypeAndNotDeleted(
        @Param("username") String username,
        @Param("contentType") ContentType contentType
    );
    
    // Count total by user
    @Query("SELECT COUNT(mgc) FROM ManualGeneratedContent mgc " +
           "WHERE mgc.user.username = :username " +
           "AND mgc.deletedAt IS NULL")
    long countByUsernameAndNotDeleted(@Param("username") String username);
    
    // Find by subject
    @Query("SELECT mgc FROM ManualGeneratedContent mgc " +
           "WHERE mgc.user.username = :username " +
           "AND mgc.subject = :subject " +
           "AND mgc.deletedAt IS NULL " +
           "ORDER BY mgc.createdAt DESC")
    List<ManualGeneratedContent> findByUsernameAndSubjectAndNotDeleted(
        @Param("username") String username,
        @Param("subject") String subject
    );
    
    // Search by title or content (for JSON search, we'll implement in service layer)
    @Query("SELECT mgc FROM ManualGeneratedContent mgc " +
           "WHERE mgc.user.username = :username " +
           "AND mgc.contentTitle LIKE %:searchTerm% " +
           "AND mgc.deletedAt IS NULL " +
           "ORDER BY mgc.createdAt DESC")
    List<ManualGeneratedContent> searchByTitleAndUsername(
        @Param("username") String username,
        @Param("searchTerm") String searchTerm
    );
}
