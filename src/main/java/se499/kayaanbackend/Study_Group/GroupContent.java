package se499.kayaanbackend.Study_Group;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "group_id", nullable = false)
    private Integer groupId;
    
    @Column(name = "uploader_id", nullable = false)
    private Integer uploaderId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "type")
    private String type; // note | quiz | flashcard
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "preview", columnDefinition = "TEXT")
    private String preview;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_url")
    private String fileUrl;
    
    @Column(name = "storage_path")
    private String storagePath;
    
    @Column(name = "mime_type", nullable = false)
    private String mimeType;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(columnDefinition = "JSON")
    private String tags;
    
    @Column(name = "stats", columnDefinition = "JSON")
    private String stats;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
