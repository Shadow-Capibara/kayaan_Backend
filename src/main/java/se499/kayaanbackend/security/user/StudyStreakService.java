package se499.kayaanbackend.security.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing study streak functionality
 * Implements flowchart logic with Freezing Count system
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudyStreakService {
    
    private final StudyStreakRepository studyStreakRepository;
    
    /**
     * Get study streak for user
     * @param userId User ID
     * @return StudyStreak entity
     */
    @Transactional(readOnly = true)
    public Optional<StudyStreak> getStreak(Long userId) {
        return studyStreakRepository.findByUserId(userId);
    }
    
    /**
     * Get or create study streak for user
     * @param userId User ID
     * @return StudyStreak entity
     */
    public StudyStreak getOrCreateStreak(Long userId) {
        return studyStreakRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = new User();
                user.setId(userId.intValue());
                return studyStreakRepository.save(StudyStreak.builder()
                    .user(user)
                    .streakCount(0)
                    .freezingCount(0)
                    .build());
            });
    }

    /**
     * Get study streak for user (read-only)
     * @param userId User ID
     * @return Optional StudyStreak
     */
    @Transactional(readOnly = true)
    public Optional<StudyStreak> getStreakOnly(Long userId) {
        return studyStreakRepository.findByUserId(userId);
    }
    
    /**
     * Complete daily task (Created Content or Interactive Mode)
     * New logic: Always increment streak when user completes task, reset freezing count to 0
     * @param userId User ID
     * @param taskType Task type (CREATED_CONTENT, INTERACTIVE_MODE)
     * @param contentId Content ID
     * @return Updated StudyStreak
     */
    public StudyStreak completeDailyTask(Long userId, String taskType, Long contentId) {
        StudyStreak streak = getOrCreateStreak(userId);
        
        // Check if user already completed daily task today
        if (streak.hasCompletedDailyTaskToday()) {
            log.info("User {} already completed daily task today - Task: {}, Content: {}", 
                userId, taskType, contentId);
            return streak; // Return existing streak without incrementing
        }
        
        // New logic: Always increment streak and reset freezing count to 0
        streak.incrementStreak();
        streak.resetFreezingCount(); // Reset freezing count when user completes task
        
        log.info("Daily task completed for user {} - Task: {}, Content: {}, Streak: {}, Freezing: {}", 
            userId, taskType, contentId, streak.getStreakCount(), streak.getFreezingCount());
        
        return studyStreakRepository.save(streak);
    }
    
    /**
     * Process daily check for a user (Start of Day Check)
     * New logic: If user didn't complete task, increment freezing count
     * If freezing_count == 2, reset streak to 0
     * @param userId User ID
     * @return Updated StudyStreak
     */
    public StudyStreak processDailyCheck(Long userId) {
        StudyStreak streak = getOrCreateStreak(userId);
        
        // Check if user completed daily task today
        if (!streak.hasCompletedDailyTaskToday()) {
            // User didn't complete daily task - increment freezing count
            streak.incrementFreezingCount();
            log.info("Daily task not completed for user {} - Freezing count: {}", 
                userId, streak.getFreezingCount());
            
            // New logic: Check if freezing_count == 2, then reset streak
            if (shouldResetStreak(streak)) {
                String reason = getResetReason(streak);
                streak.resetStreak();
                streak.resetFreezingCount();
                
                log.info("Streak reset for user {} - Reason: {}", userId, reason);
            }
        }
        
        return studyStreakRepository.save(streak);
    }
    
    /**
     * Check if streak should be reset based on new flowchart logic
     * Reset when freezing_count == 2 (simple logic)
     * @param streak StudyStreak entity
     * @return True if should reset
     */
    private boolean shouldResetStreak(StudyStreak streak) {
        // New logic: Reset streak when freezing_count == 2
        return streak.getFreezingCount() == 2;
    }
    
    /**
     * Get reset reason for logging
     * @param streak StudyStreak entity
     * @return Reset reason string
     */
    private String getResetReason(StudyStreak streak) {
        if (streak.getFreezingCount() == 2) {
            return "2 consecutive missed days";
        }
        return "Unknown reason";
    }
    
    /**
     * Process daily check for all users (Scheduled job)
     * Runs at 00:01 every day
     */
    public void processDailyCheckForAllUsers() {
        log.info("Starting daily streak check for all users");
        
        // Get all streaks that need checking
        List<StudyStreak> streaks = studyStreakRepository.findStreaksNeedingDailyCheck(
            LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
        
        for (StudyStreak streak : streaks) {
            try {
                processDailyCheck(streak.getUser().getId().longValue());
            } catch (Exception e) {
                log.error("Error processing daily check for user {}: {}", 
                    streak.getUser().getId(), e.getMessage());
            }
        }
        
        log.info("Daily streak check completed for {} users", streaks.size());
    }
    
    /**
     * Get streak status for dashboard display
     * @param userId User ID
     * @return StreakStatus DTO
     */
    @Transactional(readOnly = true)
    public StreakStatus getStreakStatus(Long userId) {
        try {
            Optional<StudyStreak> streakOpt = getStreakOnly(userId);
            
            if (streakOpt.isPresent()) {
                StudyStreak streak = streakOpt.get();
                return StreakStatus.builder()
                    .streakCount(streak.getStreakCount())
                    .freezingCount(streak.getFreezingCount())
                    .lastActivityTime(streak.getLastActivityTime())
                    .lastFreezeDate(streak.getLastFreezeDate())
                    .hasCompletedToday(streak.hasCompletedDailyTaskToday())
                    .daysSinceLastActivity(streak.getDaysSinceLastActivity())
                    .statusMessage(getStatusMessage(streak))
                    .motivationalQuote(getMotivationalQuote(streak))
                    .build();
            } else {
                // No streak exists yet - return default status
                return StreakStatus.builder()
                    .streakCount(0)
                    .freezingCount(0)
                    .hasCompletedToday(false)
                    .daysSinceLastActivity(999L)
                    .statusMessage("Start your learning journey today! Complete any content creation or interactive mode.")
                    .motivationalQuote("Every expert was once a beginner. Start your learning journey today!")
                    .build();
            }
        } catch (Exception e) {
            log.error("Error getting streak status for user: {}", userId, e);
            // Return default status on error
            return StreakStatus.builder()
                .streakCount(0)
                .freezingCount(0)
                .hasCompletedToday(false)
                .daysSinceLastActivity(999L)
                .statusMessage("Unable to load streak status")
                .motivationalQuote("Don't give up! Try again later.")
                .build();
        }
    }
    
    /**
     * Get status message for a streak (updated for new logic)
     */
    private String getStatusMessage(StudyStreak streak) {
        if (streak.hasCompletedDailyTaskToday()) {
            return "Great job! You've completed your daily task today.";
        } else if (streak.getStreakCount() == 0) {
            return "Start your learning journey today! Complete any content creation or interactive mode.";
        } else if (streak.isFreezingCountAtWarning()) {
            return String.format("You have a %d-day streak! Complete your daily task today to maintain it. (1 missed day)", streak.getStreakCount());
        } else {
            return String.format("You have a %d-day streak! Complete your daily task to maintain it.", streak.getStreakCount());
        }
    }
    
    /**
     * Get motivational quote for a streak
     */
    private String getMotivationalQuote(StudyStreak streak) {
        if (streak.getStreakCount() == 0) {
            String[] quotes = {
                "Every expert was once a beginner. Start your learning journey today!",
                "The secret to getting ahead is getting started. Begin your streak now!",
                "Success is the sum of small efforts repeated day in and day out.",
                "Don't watch the clock; do what it does. Keep going and start your streak!",
                "The future belongs to those who believe in the beauty of their dreams."
            };
            return quotes[streak.getStreakCount() % quotes.length];
        }
        return null;
    }
    
    /**
     * Reset streak manually (admin only)
     * @param userId User ID
     * @return Updated StudyStreak
     */
    public StudyStreak resetStreak(Long userId) {
        StudyStreak streak = getOrCreateStreak(userId);
        streak.resetStreak();
        streak.resetFreezingCount();
        
        log.info("Streak manually reset for user {}", userId);
        
        return studyStreakRepository.save(streak);
    }
    
    /**
     * DTO for streak status information
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StreakStatus {
        private Integer streakCount;
        private Integer freezingCount;
        private LocalDateTime lastActivityTime;
        private LocalDate lastFreezeDate;
        private Boolean hasCompletedToday;
        private Long daysSinceLastActivity;
        private String statusMessage;
        private String motivationalQuote;
        
        /**
         * Get motivational quote for streak = 0
         */
        public String getMotivationalQuote() {
            if (streakCount == 0) {
                String[] quotes = {
                    "Every expert was once a beginner. Start your learning journey today!",
                    "The secret to getting ahead is getting started. Begin your streak now!",
                    "Success is the sum of small efforts repeated day in and day out.",
                    "Don't watch the clock; do what it does. Keep going and start your streak!",
                    "The future belongs to those who believe in the beauty of their dreams."
                };
                return quotes[streakCount % quotes.length];
            }
            return null;
        }
        
        /**
         * Get status message (updated for new logic)
         */
        public String getStatusMessage() {
            if (hasCompletedToday) {
                return "Great job! You've completed your daily task today.";
            } else if (streakCount == 0) {
                return "Start your learning journey today! Complete any content creation or interactive mode.";
            } else if (freezingCount == 1) {
                return String.format("You have a %d-day streak! Complete your daily task today to maintain it. (1 missed day)", streakCount);
            } else {
                return String.format("You have a %d-day streak! Complete your daily task to maintain it.", streakCount);
            }
        }
    }
}
