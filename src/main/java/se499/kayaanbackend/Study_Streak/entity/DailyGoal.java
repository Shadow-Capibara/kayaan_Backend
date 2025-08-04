package se499.kayaanbackend.Study_Streak.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import se499.kayaanbackend.security.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daily_goals")
public class DailyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_minutes", nullable = false)
    private Integer targetMinutes;

    @Column(name = "goal_date", nullable = false)
    private LocalDate goalDate;

    @Column(name = "achieved_minutes", columnDefinition = "INTEGER DEFAULT 0")
    private Integer achievedMinutes = 0;

    @Column(name = "is_achieved", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAchieved = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Helper method to update achieved minutes
    public void updateAchievedMinutes(Integer minutes) {
        this.achievedMinutes = minutes;
        this.isAchieved = this.achievedMinutes >= this.targetMinutes;
    }

    // Helper method to add achieved minutes
    public void addAchievedMinutes(Integer minutes) {
        this.achievedMinutes += minutes;
        this.isAchieved = this.achievedMinutes >= this.targetMinutes;
    }

    // Helper method to get progress percentage
    public double getProgressPercentage() {
        if (targetMinutes == 0) return 0.0;
        return Math.min(100.0, (double) achievedMinutes / targetMinutes * 100);
    }
} 