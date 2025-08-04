package se499.kayaanbackend.Study_Streak.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import se499.kayaanbackend.security.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study_sessions")
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "subject", length = 100)
    private String subject;

    @Column(name = "session_type", length = 50)
    private String sessionType;

    @Column(name = "is_completed", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isCompleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to calculate duration
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            long durationInSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
            this.durationMinutes = (int) (durationInSeconds / 60);
        }
    }

    // Helper method to check if session is valid (minimum 5 minutes)
    public boolean isValidSession() {
        return durationMinutes != null && durationMinutes >= 5;
    }
} 