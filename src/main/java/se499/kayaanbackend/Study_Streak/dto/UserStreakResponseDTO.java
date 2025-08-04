package se499.kayaanbackend.Study_Streak.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStreakResponseDTO {

    private Long id;
    private Long userId;
    private String username;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastStudyDate;
    private Integer totalStudyDays;
    private Integer totalStudyMinutes;
    private Integer totalStudyHours;
} 