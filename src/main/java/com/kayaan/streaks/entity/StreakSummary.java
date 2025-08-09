package com.kayaan.streaks.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "streak_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreakSummary {
    @Id
    private Long userId;

    @Column(nullable = false)
    private Integer currentStreak;

    @Column(nullable = false)
    private Integer longestStreak;

    private LocalDate lastActiveDate;

    private OffsetDateTime updatedAt;
}


