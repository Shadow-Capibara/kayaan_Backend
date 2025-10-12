package se499.kayaanbackend.security.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Study Streak Entity for tracking user's study streak
 * Implements flowchart logic with Freezing Count system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study_streak")
public class StudyStreak {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "streak_count", nullable = false)
    @Builder.Default
    private Integer streakCount = 0;
    
    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;
    
    @Column(name = "freezing_count", nullable = false)
    @Builder.Default
    private Integer freezingCount = 0;
    
    @Column(name = "last_freeze_date")
    private LocalDate lastFreezeDate;
    
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
    
    /**
     * Increment streak count and update last activity time
     */
    public void incrementStreak() {
        this.streakCount++;
        this.lastActivityTime = LocalDateTime.now();
    }
    
    /**
     * Reset streak to 0
     */
    public void resetStreak() {
        this.streakCount = 0;
        this.lastActivityTime = null;
    }
    
    /**
     * Increment freezing count
     */
    public void incrementFreezingCount() {
        this.freezingCount++;
        this.lastFreezeDate = LocalDate.now();
    }
    
    /**
     * Reset freezing count to 0
     */
    public void resetFreezingCount() {
        this.freezingCount = 0;
        this.lastFreezeDate = null;
    }
    
    /**
     * Check if user completed daily task today
     */
    public boolean hasCompletedDailyTaskToday() {
        if (lastActivityTime == null) return false;
        return lastActivityTime.toLocalDate().equals(LocalDate.now());
    }
    
    /**
     * Get days since last activity
     */
    public long getDaysSinceLastActivity() {
        if (lastActivityTime == null) return 999; // Use a reasonable large number instead of Long.MAX_VALUE
        return java.time.Duration.between(lastActivityTime, LocalDateTime.now()).toDays();
    }
    
    /**
     * Check if freezing count > 1 in past week
     */
    public boolean hasMoreThanOneFreezeInPastWeek() {
        if (lastFreezeDate == null) return false;
        return freezingCount > 1 && lastFreezeDate.isAfter(LocalDate.now().minusDays(7));
    }
    
    /**
     * Check if freezing count > 2 in current month
     */
    public boolean hasMoreThanTwoFreezesInCurrentMonth() {
        if (lastFreezeDate == null) return false;
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return freezingCount > 2 && lastFreezeDate.isAfter(currentMonth);
    }
    
    /**
     * Check if user is initialized (has streak record)
     */
    public boolean isInitialized() {
        return id != null;
    }
}
