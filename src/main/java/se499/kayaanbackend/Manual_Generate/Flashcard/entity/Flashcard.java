package se499.kayaanbackend.Manual_Generate.Flashcard.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "flashcard")
public class Flashcard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "front_text", nullable = false)
    private String frontText;
    
    @Column(name = "back_text", nullable = false)
    private String backText;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false)
    private String difficulty;
    
    @Column(columnDefinition = "TEXT")
    private String tags;
    
    @Column(name = "created_by_username")
    private String createdByUsername;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
