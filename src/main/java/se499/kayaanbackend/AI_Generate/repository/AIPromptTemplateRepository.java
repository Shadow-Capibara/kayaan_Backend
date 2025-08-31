package se499.kayaanbackend.AI_Generate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.AI_Generate.entity.AIPromptTemplate;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIPromptTemplateRepository extends JpaRepository<AIPromptTemplate, Long> {
    
    // Find by user ID with pagination
    Page<AIPromptTemplate> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    // Find active templates by user ID
    List<AIPromptTemplate> findByUser_IdAndIsActiveTrue(Integer userId);
    
    // Find public active templates
    List<AIPromptTemplate> findByIsPublicTrueAndIsActiveTrue();
    
    // Find by user ID and output format
    List<AIPromptTemplate> findByUser_IdAndOutputFormatAndIsActiveTrue(Integer userId, String outputFormat);
    
    // Find by template name (case-insensitive search)
    @Query("SELECT t FROM AIPromptTemplate t WHERE t.user.id = :userId AND LOWER(t.templateName) LIKE LOWER(CONCAT('%', :name, '%')) AND t.isActive = true")
    List<AIPromptTemplate> findByUserIdAndTemplateNameContainingIgnoreCase(@Param("userId") Integer userId, @Param("name") String name);
    
    // Find popular templates (by usage count)
    @Query("SELECT t FROM AIPromptTemplate t WHERE t.isPublic = true AND t.isActive = true ORDER BY t.usageCount DESC")
    List<AIPromptTemplate> findPopularPublicTemplates(Pageable pageable);
    
    // Increment usage count
    @Query("UPDATE AIPromptTemplate t SET t.usageCount = t.usageCount + 1 WHERE t.id = :id")
    void incrementUsageCount(@Param("id") Long id);
    
    // Find templates by user ID with specific output formats
    @Query("SELECT t FROM AIPromptTemplate t WHERE t.user.id = :userId AND t.outputFormat IN :formats AND t.isActive = true")
    List<AIPromptTemplate> findByUserIdAndOutputFormatIn(@Param("userId") Integer userId, @Param("formats") List<String> formats);
}
