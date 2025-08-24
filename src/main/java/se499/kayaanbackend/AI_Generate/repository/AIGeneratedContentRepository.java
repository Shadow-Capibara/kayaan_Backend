package se499.kayaanbackend.AI_Generate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.AI_Generate.entity.AIGeneratedContent;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIGeneratedContentRepository extends JpaRepository<AIGeneratedContent, Long> {
    
    // Find by user ID with pagination
    Page<AIGeneratedContent> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    // Find saved content by user ID
    List<AIGeneratedContent> findByUser_IdAndIsSavedTrue(Integer userId);
    
    // Find by generation request ID
    List<AIGeneratedContent> findByGenerationRequestIdOrderByContentVersionDesc(Long generationRequestId);
    
    // Find by user ID and content type
    List<AIGeneratedContent> findByUser_IdAndContentTypeAndIsSavedTrue(Integer userId, String contentType);
    
    // Find latest version by generation request ID
    @Query("SELECT c FROM AIGeneratedContent c WHERE c.generationRequest.id = :requestId ORDER BY c.contentVersion DESC LIMIT 1")
    Optional<AIGeneratedContent> findLatestVersionByRequestId(@Param("requestId") Long requestId);
    
    // Find by user ID with specific content types
    @Query("SELECT c FROM AIGeneratedContent c WHERE c.user.id = :userId AND c.contentType IN :types AND c.isSaved = true ORDER BY c.createdAt DESC")
    List<AIGeneratedContent> findByUserIdAndContentTypeIn(@Param("userId") Integer userId, @Param("types") List<String> types);
    
    // Find content by title (case-insensitive search)
    @Query("SELECT c FROM AIGeneratedContent c WHERE c.user.id = :userId AND LOWER(c.contentTitle) LIKE LOWER(CONCAT('%', :title, '%')) AND c.isSaved = true")
    List<AIGeneratedContent> findByUserIdAndTitleContainingIgnoreCase(@Param("userId") Integer userId, @Param("title") String title);
    
    // Count by user ID and saved status
    long countByUser_IdAndIsSavedTrue(Integer userId);
    
    // Count by user ID and content type
    long countByUser_IdAndContentTypeAndIsSavedTrue(Integer userId, String contentType);
    
    // Find content with specific version
    Optional<AIGeneratedContent> findByGenerationRequestIdAndContentVersion(Long generationRequestId, Integer contentVersion);
}
