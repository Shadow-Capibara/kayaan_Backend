package se499.kayaanbackend.Study_Streak.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.Study_Streak.dto.UserStreakResponseDTO;
import se499.kayaanbackend.Study_Streak.service.UserStreakService;
import se499.kayaanbackend.common.response.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/streaks")
@RequiredArgsConstructor
@Tag(name = "User Streak Management", description = "APIs for managing user study streaks")
public class UserStreakController {

    private final UserStreakService userStreakService;

    @GetMapping("/current")
    @Operation(summary = "Get current streak", description = "Get the current study streak for the authenticated user")
    public ResponseEntity<ApiResponse<UserStreakResponseDTO>> getCurrentStreak(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        UserStreakResponseDTO response = userStreakService.getCurrentStreak(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Current streak retrieved successfully", response));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get streak statistics", description = "Get detailed streak statistics for the user")
    public ResponseEntity<ApiResponse<UserStreakResponseDTO>> getStreakStatistics(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        UserStreakResponseDTO response = userStreakService.getStreakStatistics(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Streak statistics retrieved successfully", response));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get streak leaderboard", description = "Get the top study streaks for all users")
    public ResponseEntity<ApiResponse<List<UserStreakResponseDTO>>> getLeaderboard() {
        
        List<UserStreakResponseDTO> response = userStreakService.getLeaderboard();
        
        return ResponseEntity.ok(ApiResponse.success("Leaderboard retrieved successfully", response));
    }

    @PostMapping("/initialize")
    @Operation(summary = "Initialize streak", description = "Initialize streak for a new user")
    public ResponseEntity<ApiResponse<Void>> initializeStreak(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        userStreakService.initializeStreak(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Streak initialized successfully", null));
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset streak", description = "Reset the current streak for the user")
    public ResponseEntity<ApiResponse<Void>> resetStreak(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        userStreakService.resetStreak(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Streak reset successfully", null));
    }

    @PostMapping("/check")
    @Operation(summary = "Check and update streak", description = "Check and update streak based on study activity")
    public ResponseEntity<ApiResponse<Void>> checkAndUpdateStreak(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        userStreakService.checkAndUpdateStreak(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Streak checked and updated successfully", null));
    }

    private Integer getUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder - you'll need to implement this based on your JWT structure
        // For now, assuming the principal contains the user ID
        return Integer.valueOf(authentication.getName());
    }
} 