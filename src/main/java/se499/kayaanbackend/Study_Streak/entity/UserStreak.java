package se499.kayaanbackend.Study_Streak.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import se499.kayaanbackend.security.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_streaks")
public class UserStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "current_streak", columnDefinition = "INTEGER DEFAULT 0")
    private Integer currentStreak = 0;

    @Column(name = "longest_streak", columnDefinition = "INTEGER DEFAULT 0")
    private Integer longestStreak = 0;

    @Column(name = "last_study_date")
    private LocalDate lastStudyDate;

    @Column(name = "total_study_days", columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalStudyDays = 0;

    @Column(name = "total_study_minutes", columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalStudyMinutes = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to increment streak
    public void incrementStreak() {
        this.currentStreak++;
        if (this.currentStreak > this.longestStreak) {
            this.longestStreak = this.currentStreak;
        }
    }

    // Helper method to reset streak
    public void resetStreak() {
        this.currentStreak = 0;
    }

    // Helper method to add study minutes
    public void addStudyMinutes(Integer minutes) {
        this.totalStudyMinutes += minutes;
    }

    // Helper method to increment study days
    public void incrementStudyDays() {
        this.totalStudyDays++;
    }
} 