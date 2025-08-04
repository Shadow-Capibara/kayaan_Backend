package se499.kayaanbackend.Study_Streak.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import se499.kayaanbackend.Study_Streak.dto.StudySessionRequestDTO;
import se499.kayaanbackend.Study_Streak.dto.StudySessionResponseDTO;
import se499.kayaanbackend.Study_Streak.service.StudySessionService;
import se499.kayaanbackend.common.response.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/study-sessions")
@RequiredArgsConstructor
@Tag(name = "Study Session Management", description = "APIs for managing study sessions")
public class StudySessionController {

    private final StudySessionService studySessionService;

    @PostMapping("/start")
    @Operation(summary = "Start a new study session", description = "Start a new study session for the authenticated user")
    public ResponseEntity<ApiResponse<StudySessionResponseDTO>> startSession(
            @Parameter(description = "Study session details") @Valid @RequestBody StudySessionRequestDTO requestDTO,
            Authentication authentication) {
        
        log.info("Starting study session for user: {}", authentication.getName());
        
        Integer userId = getUserIdFromAuthentication(authentication);
        StudySessionResponseDTO response = studySessionService.startSession(userId, requestDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Study session started successfully", response));
    }

    @PostMapping("/end")
    @Operation(summary = "End current study session", description = "End the current active study session")
    public ResponseEntity<ApiResponse<StudySessionResponseDTO>> endSession(
            @Parameter(description = "Session ID to end") @RequestParam Long sessionId,
            Authentication authentication) {
        
        log.info("Ending study session: {} for user: {}", sessionId, authentication.getName());
        
        Integer userId = getUserIdFromAuthentication(authentication);
        StudySessionResponseDTO response = studySessionService.endSession(userId, sessionId);
        
        return ResponseEntity.ok(ApiResponse.success("Study session ended successfully", response));
    }

    @GetMapping("/current")
    @Operation(summary = "Get current active session", description = "Get the current active study session for the user")
    public ResponseEntity<ApiResponse<StudySessionResponseDTO>> getCurrentSession(Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        StudySessionResponseDTO response = studySessionService.getCurrentSession(userId);
        
        if (response == null) {
            return ResponseEntity.ok(ApiResponse.success("No active session found", null));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Current session retrieved successfully", response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get session history", description = "Get paginated study session history for the user")
    public ResponseEntity<ApiResponse<Page<StudySessionResponseDTO>>> getSessionHistory(
            @Parameter(description = "Pagination parameters") Pageable pageable,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        Page<StudySessionResponseDTO> response = studySessionService.getSessionHistory(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Session history retrieved successfully", response));
    }

    @GetMapping("/history/range")
    @Operation(summary = "Get sessions by date range", description = "Get study sessions within a specific date range")
    public ResponseEntity<ApiResponse<List<StudySessionResponseDTO>>> getSessionsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        List<StudySessionResponseDTO> response = studySessionService.getSessionsByDateRange(userId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Sessions retrieved successfully", response));
    }

    @PutMapping("/{sessionId}")
    @Operation(summary = "Update study session", description = "Update an existing study session")
    public ResponseEntity<ApiResponse<StudySessionResponseDTO>> updateSession(
            @Parameter(description = "Session ID to update") @PathVariable Long sessionId,
            @Parameter(description = "Updated session details") @Valid @RequestBody StudySessionRequestDTO requestDTO,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        StudySessionResponseDTO response = studySessionService.updateSession(userId, sessionId, requestDTO);
        
        return ResponseEntity.ok(ApiResponse.success("Study session updated successfully", response));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Delete study session", description = "Delete a study session")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @Parameter(description = "Session ID to delete") @PathVariable Long sessionId,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        studySessionService.deleteSession(userId, sessionId);
        
        return ResponseEntity.ok(ApiResponse.success("Study session deleted successfully", null));
    }

    @GetMapping("/statistics/daily")
    @Operation(summary = "Get daily study statistics", description = "Get total study minutes for a specific date")
    public ResponseEntity<ApiResponse<Integer>> getDailyStudyMinutes(
            @Parameter(description = "Date to get statistics for (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            Authentication authentication) {
        
        Integer userId = getUserIdFromAuthentication(authentication);
        Integer totalMinutes = studySessionService.getTotalStudyMinutesForDate(userId, date);
        
        return ResponseEntity.ok(ApiResponse.success("Daily statistics retrieved successfully", totalMinutes));
    }

    private Integer getUserIdFromAuthentication(Authentication authentication) {
        // This is a placeholder - you'll need to implement this based on your JWT structure
        // For now, assuming the principal contains the user ID
        return Integer.valueOf(authentication.getName());
    }
} 