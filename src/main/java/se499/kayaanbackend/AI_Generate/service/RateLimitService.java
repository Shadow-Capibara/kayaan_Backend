package se499.kayaanbackend.AI_Generate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting service for AI generation requests
 * Prevents abuse and helps control costs
 */
@Slf4j
@Service
public class RateLimitService {

    @Value("${ai.generation.rate-limit.max-requests-per-hour:5}")
    private int maxRequestsPerHour;

    @Value("${ai.generation.rate-limit.max-requests-per-minute:3}")
    private int maxRequestsPerMinute;

    @Value("${ai.generation.rate-limit.max-requests-per-day:50}")
    private int maxRequestsPerDay;

    // User-based rate limiting
    private final Map<String, UserRateLimit> userRateLimits = new ConcurrentHashMap<>();

    /**
     * Check if user can make AI generation request
     */
    public boolean canMakeRequest(String userId) {
        UserRateLimit userLimit = userRateLimits.computeIfAbsent(userId, k -> new UserRateLimit());
        
        LocalDateTime now = LocalDateTime.now();
        
        // Check daily limit
        if (userLimit.getDailyCount() >= maxRequestsPerDay) {
            log.warn("User {} exceeded daily rate limit: {}", userId, maxRequestsPerDay);
            return false;
        }
        
        // Check hourly limit
        if (userLimit.getHourlyCount(now) >= maxRequestsPerHour) {
            log.warn("User {} exceeded hourly rate limit: {}", userId, maxRequestsPerHour);
            return false;
        }
        
        // Check minute limit
        if (userLimit.getMinuteCount(now) >= maxRequestsPerMinute) {
            log.warn("User {} exceeded minute rate limit: {}", userId, maxRequestsPerMinute);
            return false;
        }
        
        return true;
    }

    /**
     * Record a successful request
     */
    public void recordRequest(String userId) {
        UserRateLimit userLimit = userRateLimits.get(userId);
        if (userLimit != null) {
            userLimit.recordRequest();
            log.debug("Recorded request for user: {}", userId);
        }
    }

    /**
     * Get current usage for user
     */
    public RateLimitInfo getCurrentUsage(String userId) {
        UserRateLimit userLimit = userRateLimits.get(userId);
        if (userLimit == null) {
            return new RateLimitInfo(0, 0, 0, maxRequestsPerHour, maxRequestsPerMinute, maxRequestsPerDay);
        }
        
        LocalDateTime now = LocalDateTime.now();
        return new RateLimitInfo(
            userLimit.getDailyCount(),
            userLimit.getHourlyCount(now),
            userLimit.getMinuteCount(now),
            maxRequestsPerHour,
            maxRequestsPerMinute,
            maxRequestsPerDay
        );
    }

    /**
     * Reset rate limits for testing
     */
    public void resetRateLimits(String userId) {
        userRateLimits.remove(userId);
        log.info("Reset rate limits for user: {}", userId);
    }

    /**
     * Inner class to track rate limits per user
     */
    private static class UserRateLimit {
        private final AtomicInteger dailyCount = new AtomicInteger(0);
        private final AtomicInteger hourlyCount = new AtomicInteger(0);
        private final AtomicInteger minuteCount = new AtomicInteger(0);
        
        private LocalDateTime lastReset = LocalDateTime.now();
        private LocalDateTime lastHourReset = LocalDateTime.now();
        private LocalDateTime lastMinuteReset = LocalDateTime.now();

        public void recordRequest() {
            dailyCount.incrementAndGet();
            hourlyCount.incrementAndGet();
            minuteCount.incrementAndGet();
        }

        public int getDailyCount() {
            LocalDateTime now = LocalDateTime.now();
            if (now.toLocalDate().isAfter(lastReset.toLocalDate())) {
                dailyCount.set(0);
                lastReset = now;
            }
            return dailyCount.get();
        }

        public int getHourlyCount(LocalDateTime now) {
            if (now.getHour() != lastHourReset.getHour() || 
                now.toLocalDate().isAfter(lastHourReset.toLocalDate())) {
                hourlyCount.set(0);
                lastHourReset = now;
            }
            return hourlyCount.get();
        }

        public int getMinuteCount(LocalDateTime now) {
            if (now.getMinute() != lastMinuteReset.getMinute() || 
                now.getHour() != lastMinuteReset.getHour() ||
                now.toLocalDate().isAfter(lastMinuteReset.toLocalDate())) {
                minuteCount.set(0);
                lastMinuteReset = now;
            }
            return minuteCount.get();
        }
    }

    /**
     * Rate limit information for a user
     */
    public static class RateLimitInfo {
        private final int dailyUsed;
        private final int hourlyUsed;
        private final int minuteUsed;
        private final int hourlyLimit;
        private final int minuteLimit;
        private final int dailyLimit;

        public RateLimitInfo(int dailyUsed, int hourlyUsed, int minuteUsed, 
                           int hourlyLimit, int minuteLimit, int dailyLimit) {
            this.dailyUsed = dailyUsed;
            this.hourlyUsed = hourlyUsed;
            this.minuteUsed = minuteUsed;
            this.hourlyLimit = hourlyLimit;
            this.minuteLimit = minuteLimit;
            this.dailyLimit = dailyLimit;
        }

        // Getters
        public int getDailyUsed() { return dailyUsed; }
        public int getHourlyUsed() { return hourlyUsed; }
        public int getMinuteUsed() { return minuteUsed; }
        public int getHourlyLimit() { return hourlyLimit; }
        public int getMinuteLimit() { return minuteLimit; }
        public int getDailyLimit() { return dailyLimit; }
        public int getDailyRemaining() { return dailyLimit - dailyUsed; }
        public int getHourlyRemaining() { return hourlyLimit - hourlyUsed; }
        public int getMinuteRemaining() { return minuteLimit - minuteUsed; }
    }
}
