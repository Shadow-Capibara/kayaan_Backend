package se499.kayaanbackend.Study_Streak.service;

import java.util.List;

import se499.kayaanbackend.Study_Streak.dto.UserStreakResponseDTO;

public interface UserStreakService {

    // Get current streak for user
    UserStreakResponseDTO getCurrentStreak(Integer userId);

    // Update streak after study session
    void updateStreakAfterSession(Integer userId, Integer studyMinutes);

    // Get streak statistics
    UserStreakResponseDTO getStreakStatistics(Integer userId);

    // Get leaderboard
    List<UserStreakResponseDTO> getLeaderboard();

    // Initialize streak for new user
    void initializeStreak(Integer userId);

    // Reset streak for user
    void resetStreak(Integer userId);

    // Check and update streak based on study activity
    void checkAndUpdateStreak(Integer userId);
} 