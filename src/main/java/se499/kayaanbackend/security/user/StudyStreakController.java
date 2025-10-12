package se499.kayaanbackend.security.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for Study Streak API endpoints
 * Implements flowchart logic with Freezing Count system
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class StudyStreakController {
    
    private final StudyStreakService studyStreakService;
    
    /**
     * GET /users/{userId}/streak - Get user's streak information
     */
    @GetMapping("/{userId}/streak")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudyStreakService.StreakStatus> getStreak(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            // Check if user can access this streak (own streak or admin)
            boolean isAdmin = currentUser.getRoles().contains(Role.ROLE_ADMIN);
            if (!currentUser.getId().equals(userId.intValue()) && !isAdmin) {
                return ResponseEntity.status(403).build();
            }
            
            log.info("Getting streak for user: {}", userId);
            
            StudyStreakService.StreakStatus streakStatus = studyStreakService.getStreakStatus(userId);
            
            return ResponseEntity.ok(streakStatus);
            
        } catch (Exception e) {
            log.error("Error getting streak for user: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * POST /users/{userId}/streak/complete-task - Complete daily task
     * Task types: CREATED_CONTENT, INTERACTIVE_MODE
     */
    @PostMapping("/{userId}/streak/complete-task")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> completeDailyTask(
            @PathVariable Long userId,
            @RequestBody TaskCompletionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            // Check if user can access this streak
            boolean isAdmin = currentUser.getRoles().contains(Role.ROLE_ADMIN);
            if (!currentUser.getId().equals(userId.intValue()) && !isAdmin) {
                return ResponseEntity.status(403).build();
            }
            
            log.info("Completing daily task for user: {} - Type: {}, Content: {}", 
                userId, request.getTaskType(), request.getContentId());
            
            StudyStreak streak = studyStreakService.completeDailyTask(
                userId, request.getTaskType(), request.getContentId());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Daily task completed successfully",
                "streakCount", streak.getStreakCount(),
                "freezingCount", streak.getFreezingCount(),
                "lastActivityTime", streak.getLastActivityTime()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error completing daily task for user: {}", userId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Failed to complete daily task"));
        }
    }
    
    /**
     * GET /users/{userId}/streak/status - Get detailed streak status for dashboard
     * Uses the same logic as /streak endpoint to avoid 500 errors
     */
    @GetMapping("/{userId}/streak/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getStreakStatus(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            // Check if user can access this streak
            boolean isAdmin = currentUser.getRoles().contains(Role.ROLE_ADMIN);
            boolean isOwner = currentUser.getId() != null && currentUser.getId().equals(userId.intValue());
            if (!isOwner && !isAdmin) {
                log.warn("Access denied for user {} trying to access streak for user {}", currentUser.getId(), userId);
                return ResponseEntity.status(403).build();
            }
            
            log.info("Getting streak status for user: {}", userId);
            
            // Use the same logic as getStreak method to avoid errors
            StudyStreakService.StreakStatus status = studyStreakService.getStreakStatus(userId);
            
            // Return the same format as /streak endpoint
            Map<String, Object> response = new HashMap<>();
            response.put("streakCount", status.getStreakCount() != null ? status.getStreakCount().intValue() : 0);
            response.put("freezingCount", status.getFreezingCount() != null ? status.getFreezingCount().intValue() : 0);
            response.put("lastActivityTime", status.getLastActivityTime());
            response.put("lastFreezeDate", status.getLastFreezeDate());
            response.put("hasCompletedToday", status.getHasCompletedToday() != null ? status.getHasCompletedToday().booleanValue() : false);
            response.put("daysSinceLastActivity", status.getDaysSinceLastActivity() != null ? status.getDaysSinceLastActivity().longValue() : 999L);
            response.put("statusMessage", status.getStatusMessage() != null ? status.getStatusMessage() : "No status available");
            response.put("motivationalQuote", status.getMotivationalQuote());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting streak status for user: {}", userId, e);
            // Return default response instead of 500 error
            Map<String, Object> defaultResponse = new HashMap<>();
            defaultResponse.put("streakCount", 0);
            defaultResponse.put("freezingCount", 0);
            defaultResponse.put("lastActivityTime", null);
            defaultResponse.put("lastFreezeDate", null);
            defaultResponse.put("hasCompletedToday", false);
            defaultResponse.put("daysSinceLastActivity", 999L);
            defaultResponse.put("statusMessage", "Error loading streak data");
            defaultResponse.put("motivationalQuote", null);
            return ResponseEntity.ok(defaultResponse);
        }
    }
    
    /**
     * POST /users/{userId}/streak/daily-check - Manual daily check (for testing)
     */
    @PostMapping("/{userId}/streak/daily-check")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> processDailyCheck(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            // Check if user can access this streak
            boolean isAdmin = currentUser.getRoles().contains(Role.ROLE_ADMIN);
            if (!currentUser.getId().equals(userId.intValue()) && !isAdmin) {
                return ResponseEntity.status(403).build();
            }
            
            log.info("Processing daily check for user: {}", userId);
            
            StudyStreak streak = studyStreakService.processDailyCheck(userId);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Daily check processed successfully",
                "streakCount", streak.getStreakCount(),
                "freezingCount", streak.getFreezingCount(),
                "lastActivityTime", streak.getLastActivityTime()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing daily check for user: {}", userId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Failed to process daily check"));
        }
    }
    
    /**
     * DELETE /users/{userId}/streak - Reset streak (admin only)
     */
    @DeleteMapping("/{userId}/streak")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetStreak(
            @PathVariable Long userId
    ) {
        try {
            log.info("Resetting streak for user: {} (admin action)", userId);
            
            StudyStreak streak = studyStreakService.resetStreak(userId);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Streak reset successfully",
                "streakCount", streak.getStreakCount(),
                "freezingCount", streak.getFreezingCount()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error resetting streak for user: {}", userId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Failed to reset streak"));
        }
    }
    
    /**
     * DTO for task completion request
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TaskCompletionRequest {
        private String taskType; // CREATED_CONTENT, INTERACTIVE_MODE
        private Long contentId;
        private String metadata; // Optional additional data
    }
}
