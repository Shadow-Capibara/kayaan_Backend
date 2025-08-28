package se499.kayaanbackend.AI_Generate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.security.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_generation_request")
public class AIGenerationRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "prompt_text", nullable = false, columnDefinition = "TEXT")
    private String promptText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "output_format", nullable = false)
    private ContentType outputFormat; // FLASHCARD, QUIZ, NOTE
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenerationStatus status = GenerationStatus.PENDING;
    
    @Column(nullable = false)
    private Integer progress = 0; // 0-100
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum GenerationStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
    }
}
