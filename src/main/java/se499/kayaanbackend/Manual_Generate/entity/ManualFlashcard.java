package se499.kayaanbackend.Manual_Generate.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.security.user.User;

@Entity
@Table(name = "manual_flashcard")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualFlashcard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "front_text", columnDefinition = "TEXT", nullable = false)
    private String frontText;
    
    @Column(name = "back_text", columnDefinition = "TEXT", nullable = false)
    private String backText;
    
    private String subject;
    
    private String difficulty;
    
    @Column(columnDefinition = "TEXT")
    private String tags;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Soft delete method
    public void markAsDeleted() {
        deletedAt = LocalDateTime.now();
    }
    
    // Check if deleted
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
