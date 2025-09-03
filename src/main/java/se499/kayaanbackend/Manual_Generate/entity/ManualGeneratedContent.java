package se499.kayaanbackend.Manual_Generate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.AI_Generate.entity.ContentType;
import se499.kayaanbackend.security.user.User;

import java.time.LocalDateTime;

/**
 * Entity for storing Manual Generation content in JSON format
 * This mirrors the structure of AIGeneratedContent for consistency
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "manual_generated_content")
public class ManualGeneratedContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "content_title", nullable = false)
    private String contentTitle;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType; // FLASHCARD, QUIZ, NOTE
    
    @Column(name = "content_data", nullable = false, columnDefinition = "JSON")
    private String contentData; // Manual content in JSON format
    
    @Column(name = "content_version")
    @Builder.Default
    private Integer contentVersion = 1;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "difficulty")
    private String difficulty;
    
    @Column(name = "tags")
    private String tags; // Comma-separated tags
    
    @Column(name = "is_saved")
    @Builder.Default
    private Boolean isSaved = true; // Always true for manual content
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // For soft delete
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    public void softDelete() {
        deletedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    public void restore() {
        deletedAt = null;
        updatedAt = LocalDateTime.now();
    }
}
