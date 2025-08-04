package se499.kayaanbackend.Study_Streak.service;

import java.time.LocalDate;
import java.util.List;

import se499.kayaanbackend.Study_Streak.dto.DailyGoalRequestDTO;
import se499.kayaanbackend.Study_Streak.dto.DailyGoalResponseDTO;

public interface DailyGoalService {

    // Create daily goal
    DailyGoalResponseDTO createDailyGoal(Integer userId, DailyGoalRequestDTO requestDTO);

    // Get current daily goal
    DailyGoalResponseDTO getCurrentDailyGoal(Integer userId);

    // Update daily goal
    DailyGoalResponseDTO updateDailyGoal(Integer userId, Long goalId, DailyGoalRequestDTO requestDTO);

    // Get daily goal history
    List<DailyGoalResponseDTO> getDailyGoalHistory(Integer userId);

    // Update achieved minutes for current goal
    void updateAchievedMinutes(Integer userId, Integer minutes);

    // Get goal statistics
    DailyGoalResponseDTO getGoalStatistics(Integer userId);

    // Check if goal is achieved
    boolean isGoalAchieved(Integer userId, LocalDate date);

    // Get goal progress percentage
    double getGoalProgressPercentage(Integer userId, LocalDate date);
} 