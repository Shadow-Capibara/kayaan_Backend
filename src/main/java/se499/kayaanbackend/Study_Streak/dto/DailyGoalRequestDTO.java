package se499.kayaanbackend.Study_Streak.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyGoalRequestDTO {

    @NotNull(message = "Target minutes is required")
    @Min(value = 1, message = "Target minutes must be at least 1")
    private Integer targetMinutes;
} 