package se499.kayaanbackend.AI_Generate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.AI_Generate.entity.AIGenerationRequest;
import se499.kayaanbackend.AI_Generate.entity.AIGenerationRequest.GenerationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AIGenerationRequestRepository extends JpaRepository<AIGenerationRequest, Long> {
    
    // Find by user ID with pagination
    Page<AIGenerationRequest> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    // Find by user ID and status
    List<AIGenerationRequest> findByUser_IdAndStatus(Integer userId, GenerationStatus status);
    
    // Find by status
    List<AIGenerationRequest> findByStatus(GenerationStatus status);
    
    // Find pending requests older than specified time (for cleanup)
    List<AIGenerationRequest> findByStatusAndCreatedAtBefore(GenerationStatus status, LocalDateTime before);
    
    // Find by user ID and output format
    List<AIGenerationRequest> findByUser_IdAndOutputFormat(Integer userId, String outputFormat);
    
    // Count by user ID
    long countByUser_Id(Integer userId);
    
    // Count by user ID and status
    long countByUser_IdAndStatus(Integer userId, GenerationStatus status);
    
    // Find requests that can be retried
    @Query("SELECT r FROM AIGenerationRequest r WHERE r.user.id = :userId AND r.status = 'FAILED' AND r.retryCount < r.maxRetries")
    List<AIGenerationRequest> findRetryableRequests(@Param("userId") Integer userId);
    
    // Find requests by user ID with specific statuses
    @Query("SELECT r FROM AIGenerationRequest r WHERE r.user.id = :userId AND r.status IN :statuses ORDER BY r.createdAt DESC")
    List<AIGenerationRequest> findByUserIdAndStatusIn(@Param("userId") Integer userId, @Param("statuses") List<GenerationStatus> statuses);
}
