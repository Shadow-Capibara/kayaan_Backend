package se499.kayaanbackend.security.user;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled service for Study Streak daily management
 * Implements flowchart logic with daily check at 00:01
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudyStreakScheduledService {
    
    private final StudyStreakService studyStreakService;
    
    /**
     * Daily job to process streak check for all users
     * Runs every day at 00:01 (Start of Day Check)
     * Implements flowchart logic
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void processDailyStreakCheck() {
        log.info("Starting daily streak check job at 00:01");
        
        try {
            studyStreakService.processDailyCheckForAllUsers();
            log.info("Daily streak check job completed successfully");
        } catch (Exception e) {
            log.error("Error in daily streak check job", e);
        }
    }
    
    /**
     * Weekly job to reset freezing counts (if needed)
     * Runs every Sunday at 01:00
     */
    @Scheduled(cron = "0 0 1 * * SUN")
    public void resetWeeklyFreezingCounts() {
        log.info("Starting weekly freezing count reset job");
        
        try {
            // This could be implemented if needed for weekly resets
            // For now, the freezing count logic is handled in the daily check
            log.info("Weekly freezing count reset job completed successfully");
        } catch (Exception e) {
            log.error("Error in weekly freezing count reset job", e);
        }
    }
    
    /**
     * Monthly job to reset freezing counts (if needed)
     * Runs on the 1st of every month at 02:00
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void resetMonthlyFreezingCounts() {
        log.info("Starting monthly freezing count reset job");
        
        try {
            // This could be implemented if needed for monthly resets
            // For now, the freezing count logic is handled in the daily check
            log.info("Monthly freezing count reset job completed successfully");
        } catch (Exception e) {
            log.error("Error in monthly freezing count reset job", e);
        }
    }
    
    /**
     * Hourly job to check for streak warnings (optional)
     * Runs every hour to check if users are approaching streak expiry
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkStreakWarnings() {
        log.debug("Starting hourly streak warning check");
        
        try {
            // This could be implemented to send notifications
            // when users are approaching streak expiry
            log.debug("Hourly streak warning check completed");
        } catch (Exception e) {
            log.error("Error in hourly streak warning check", e);
        }
    }
}
