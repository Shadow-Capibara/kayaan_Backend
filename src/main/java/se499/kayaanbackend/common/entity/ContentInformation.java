package se499.kayaanbackend.common.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_information")
public class ContentInformation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Column(nullable = false)
    private String contentType; // NOTE, FLASHCARD, QUIZ
    
    @Column(nullable = false)
    private String createdByUsername;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false)
    private String difficulty;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private Integer estimatedTime; // in minutes
    
    @Column(nullable = false)
    private String language;
    
    @Column(nullable = false)
    private String version;
    
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