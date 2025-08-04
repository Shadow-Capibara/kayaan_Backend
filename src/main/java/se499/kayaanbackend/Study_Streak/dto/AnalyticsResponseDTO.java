package se499.kayaanbackend.Study_Streak.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDTO {

    private Long userId;
    private String username;
    private Integer totalStudyMinutes;
    private Integer totalStudyHours;
    private Integer totalStudyDays;
    private Integer currentStreak;
    private Integer longestStreak;
    private Double averageMinutesPerDay;
    private List<DailyStudyData> dailyData;
    private Map<String, Integer> subjectBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStudyData {
        private LocalDate date;
        private Integer studyMinutes;
        private Boolean goalAchieved;
    }
} 