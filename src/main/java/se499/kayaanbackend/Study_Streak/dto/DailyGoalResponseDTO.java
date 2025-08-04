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
public class DailyGoalResponseDTO {

    private Long id;
    private Long userId;
    private String username;
    private Integer targetMinutes;
    private LocalDate goalDate;
    private Integer achievedMinutes;
    private Boolean isAchieved;
    private Double progressPercentage;
} 