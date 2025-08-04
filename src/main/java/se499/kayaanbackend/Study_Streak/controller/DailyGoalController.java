package se499.kayaanbackend.Study_Streak.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.Study_Streak.dto.DailyGoalRequestDTO;
import se499.kayaanbackend.Study_Streak.dto.DailyGoalResponseDTO;
import se499.kayaanbackend.Study_Streak.service.DailyGoalService;
import se499.kayaanbackend.common.response.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/goals/daily")
@RequiredArgsConstructor
@Tag(name = "Daily Goal Management", description = "APIs for managing daily study goals")
public class DailyGoalController {

    private final DailyGoalService dailyGoalService;

    @PostMapping
    @Operation(summary = "Create daily goal", description = "Create a new daily study goal for the authenticated user")
    public ResponseEntity<ApiResponse<DailyGoalResponseDTO>> createDailyGoal(
            @Parameter(description = "Daily goal details") @Valid @RequestBody DailyGoalRequestDTO requestDTO,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        DailyGoalResponseDTO response = dailyGoalService.createDailyGoal(userId, requestDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Daily goal created successfully", response));
    }

    @GetMapping("/current")
    @Operation(summary = "Get current daily goal", description = "Get the current daily goal for the user")
    public ResponseEntity<ApiResponse<DailyGoalResponseDTO>> getCurrentDailyGoal(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        DailyGoalResponseDTO response = dailyGoalService.getCurrentDailyGoal(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Current daily goal retrieved successfully", response));
    }

    @PutMapping("/{goalId}")
    @Operation(summary = "Update daily goal", description = "Update an existing daily goal")
    public ResponseEntity<ApiResponse<DailyGoalResponseDTO>> updateDailyGoal(
            @Parameter(description = "Goal ID to update") @PathVariable Long goalId,
            @Parameter(description = "Updated goal details") @Valid @RequestBody DailyGoalRequestDTO requestDTO,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        DailyGoalResponseDTO response = dailyGoalService.updateDailyGoal(userId, goalId, requestDTO);
        
        return ResponseEntity.ok(ApiResponse.success("Daily goal updated successfully", response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get daily goal history", description = "Get the history of daily goals for the user")
    public ResponseEntity<ApiResponse<List<DailyGoalResponseDTO>>> getDailyGoalHistory(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        List<DailyGoalResponseDTO> response = dailyGoalService.getDailyGoalHistory(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Daily goal history retrieved successfully", response));
    }

    @PostMapping("/update-achieved")
    @Operation(summary = "Update achieved minutes", description = "Update the achieved minutes for the current daily goal")
    public ResponseEntity<ApiResponse<Void>> updateAchievedMinutes(
            @Parameter(description = "Minutes to add to achieved total") @RequestParam Integer minutes,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        dailyGoalService.updateAchievedMinutes(userId, minutes);
        
        return ResponseEntity.ok(ApiResponse.success("Achieved minutes updated successfully", null));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get goal statistics", description = "Get statistics for the current daily goal")
    public ResponseEntity<ApiResponse<DailyGoalResponseDTO>> getGoalStatistics(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        DailyGoalResponseDTO response = dailyGoalService.getGoalStatistics(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Goal statistics retrieved successfully", response));
    }

    private Integer getUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder - you'll need to implement this based on your JWT structure
        // For now, assuming the principal contains the user ID
        return Integer.valueOf(authentication.getName());
    }
} 