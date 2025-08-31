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
@Table(name = "ai_prompt_template")
public class AIPromptTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "template_name", nullable = false)
    private String templateName;
    
    @Column(name = "template_description", columnDefinition = "TEXT")
    private String templateDescription;
    
    @Column(name = "prompt_text", nullable = false, columnDefinition = "TEXT")
    private String promptText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "output_format", nullable = false)
    private ContentType outputFormat; // FLASHCARD, QUIZ, NOTE
    
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
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
