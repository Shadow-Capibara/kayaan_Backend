package se499.kayaanbackend.Study_Streak.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Study_Streak.dto.AnalyticsResponseDTO;
import se499.kayaanbackend.Study_Streak.service.AnalyticsService;
import se499.kayaanbackend.common.response.ApiResponse;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Study Analytics", description = "APIs for study analytics and reports")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/daily")
    @Operation(summary = "Get daily analytics", description = "Get daily study analytics for the user")
    public ResponseEntity<ApiResponse<AnalyticsResponseDTO>> getDailyAnalytics(
            @Parameter(description = "Date for analytics (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        AnalyticsResponseDTO response = analyticsService.getDailyAnalytics(userId, date);
        
        return ResponseEntity.ok(ApiResponse.success("Daily analytics retrieved successfully", response));
    }

    @GetMapping("/weekly")
    @Operation(summary = "Get weekly analytics", description = "Get weekly study analytics for the user")
    public ResponseEntity<ApiResponse<AnalyticsResponseDTO>> getWeeklyAnalytics(
            @Parameter(description = "Start date of week (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        AnalyticsResponseDTO response = analyticsService.getWeeklyAnalytics(userId, startDate);
        
        return ResponseEntity.ok(ApiResponse.success("Weekly analytics retrieved successfully", response));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly analytics", description = "Get monthly study analytics for the user")
    public ResponseEntity<ApiResponse<AnalyticsResponseDTO>> getMonthlyAnalytics(
            @Parameter(description = "Year and month (yyyy-MM)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") String yearMonth,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        AnalyticsResponseDTO response = analyticsService.getMonthlyAnalytics(userId, yearMonth);
        
        return ResponseEntity.ok(ApiResponse.success("Monthly analytics retrieved successfully", response));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get study trends", description = "Get study trends and patterns for the user")
    public ResponseEntity<ApiResponse<AnalyticsResponseDTO>> getStudyTrends(
            @Parameter(description = "Number of days to analyze") @RequestParam(defaultValue = "30") Integer days,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        AnalyticsResponseDTO response = analyticsService.getStudyTrends(userId, days);
        
        return ResponseEntity.ok(ApiResponse.success("Study trends retrieved successfully", response));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get study summary", description = "Get overall study summary for the user")
    public ResponseEntity<ApiResponse<AnalyticsResponseDTO>> getStudySummary(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        AnalyticsResponseDTO response = analyticsService.getStudySummary(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Study summary retrieved successfully", response));
    }

    private Integer getUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder - you'll need to implement this based on your JWT structure
        // For now, assuming the principal contains the user ID
        return Integer.valueOf(authentication.getName());
    }
} 