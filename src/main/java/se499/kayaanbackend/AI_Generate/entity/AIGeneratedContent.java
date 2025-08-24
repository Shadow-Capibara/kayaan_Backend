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
@Table(name = "ai_generated_content")
public class AIGeneratedContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_request_id", nullable = false)
    private AIGenerationRequest generationRequest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "content_title", nullable = false)
    private String contentTitle;
    
    @Column(name = "content_type", nullable = false)
    private String contentType; // e.g., "flashcard", "quiz", "note", "summary"
    
    @Column(name = "content_data", nullable = false, columnDefinition = "JSON")
    private String contentData; // AI-generated content in JSON format
    
    @Column(name = "content_version")
    private Integer contentVersion = 1;
    
    @Column(name = "supabase_file_path")
    private String supabaseFilePath; // Path to JSON file in Supabase Storage
    
    @Column(name = "file_size")
    private Long fileSize; // File size in bytes
    
    @Column(name = "is_saved")
    private Boolean isSaved = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
