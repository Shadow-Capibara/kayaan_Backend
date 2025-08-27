package se499.kayaanbackend.AI_Generate.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service to monitor and track OpenAI API token usage
 * Helps control costs and optimize usage
 */
@Slf4j
@Service
public class TokenUsageService {

    // Global token usage tracking
    private final AtomicLong totalInputTokens = new AtomicLong(0);
    private final AtomicLong totalOutputTokens = new AtomicLong(0);
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    
    // Daily tracking
    private final AtomicLong dailyInputTokens = new AtomicLong(0);
    private final AtomicLong dailyOutputTokens = new AtomicLong(0);
    private final AtomicInteger dailyRequests = new AtomicInteger(0);
    private LocalDateTime lastDailyReset = LocalDateTime.now();
    
    // User-based tracking
    private final Map<String, UserTokenUsage> userTokenUsage = new ConcurrentHashMap<>();

    /**
     * Record token usage for a request
     */
    public void recordTokenUsage(String userId, int inputTokens, int outputTokens) {
        // Update global counters
        totalInputTokens.addAndGet(inputTokens);
        totalOutputTokens.addAndGet(outputTokens);
        totalRequests.incrementAndGet();
        
        // Update daily counters
        dailyInputTokens.addAndGet(inputTokens);
        dailyOutputTokens.addAndGet(outputTokens);
        dailyRequests.incrementAndGet();
        
        // Update user counters
        UserTokenUsage userUsage = userTokenUsage.computeIfAbsent(userId, k -> new UserTokenUsage());
        userUsage.recordUsage(inputTokens, outputTokens);
        
        // Reset daily counters if needed
        resetDailyCountersIfNeeded();
        
        log.debug("Recorded token usage - User: {}, Input: {}, Output: {}", 
                 userId, inputTokens, outputTokens);
    }

    /**
     * Get current token usage statistics
     */
    public TokenUsageStats getCurrentUsage() {
        resetDailyCountersIfNeeded();
        
        return new TokenUsageStats(
            totalInputTokens.get(),
            totalOutputTokens.get(),
            totalRequests.get(),
            dailyInputTokens.get(),
            dailyOutputTokens.get(),
            dailyRequests.get()
        );
    }

    /**
     * Get user-specific token usage
     */
    public UserTokenUsage getUserUsage(String userId) {
        return userTokenUsage.getOrDefault(userId, new UserTokenUsage());
    }

    /**
     * Estimate cost based on current usage
     */
    public CostEstimate estimateCost() {
        TokenUsageStats stats = getCurrentUsage();
        
        // GPT-5 Nano pricing (per 1M tokens)
        double inputCostPerMillion = 0.050;
        double outputCostPerMillion = 0.400;
        
        double totalInputCost = (stats.getTotalInputTokens() / 1_000_000.0) * inputCostPerMillion;
        double totalOutputCost = (stats.getTotalOutputTokens() / 1_000_000.0) * outputCostPerMillion;
        double totalCost = totalInputCost + totalOutputCost;
        
        double dailyInputCost = (stats.getDailyInputTokens() / 1_000_000.0) * inputCostPerMillion;
        double dailyOutputCost = (stats.getDailyOutputTokens() / 1_000_000.0) * outputCostPerMillion;
        double dailyCost = dailyInputCost + dailyOutputCost;
        
        return new CostEstimate(totalCost, dailyCost, inputCostPerMillion, outputCostPerMillion);
    }

    /**
     * Get cost projection for the month
     */
    public double getMonthlyCostProjection() {
        CostEstimate currentCost = estimateCost();
        int daysInMonth = LocalDateTime.now().getMonth().length(LocalDateTime.now().toLocalDate().isLeapYear());
        int currentDay = LocalDateTime.now().getDayOfMonth();
        
        if (currentDay == 0) return 0.0;
        
        double dailyAverage = currentCost.getDailyCost();
        return dailyAverage * daysInMonth;
    }

    /**
     * Reset all counters (for testing)
     */
    public void resetAllCounters() {
        totalInputTokens.set(0);
        totalOutputTokens.set(0);
        totalRequests.set(0);
        dailyInputTokens.set(0);
        dailyOutputTokens.set(0);
        dailyRequests.set(0);
        userTokenUsage.clear();
        lastDailyReset = LocalDateTime.now();
        log.info("Reset all token usage counters");
    }

    /**
     * Reset daily counters if a new day has started
     */
    private void resetDailyCountersIfNeeded() {
        LocalDateTime now = LocalDateTime.now();
        if (now.toLocalDate().isAfter(lastDailyReset.toLocalDate())) {
            dailyInputTokens.set(0);
            dailyOutputTokens.set(0);
            dailyRequests.set(0);
            lastDailyReset = now;
            log.info("Reset daily token usage counters");
        }
    }

    /**
     * Token usage statistics
     */
    public static class TokenUsageStats {
        private final long totalInputTokens;
        private final long totalOutputTokens;
        private final int totalRequests;
        private final long dailyInputTokens;
        private final long dailyOutputTokens;
        private final int dailyRequests;

        public TokenUsageStats(long totalInputTokens, long totalOutputTokens, int totalRequests,
                             long dailyInputTokens, long dailyOutputTokens, int dailyRequests) {
            this.totalInputTokens = totalInputTokens;
            this.totalOutputTokens = totalOutputTokens;
            this.totalRequests = totalRequests;
            this.dailyInputTokens = dailyInputTokens;
            this.dailyOutputTokens = dailyOutputTokens;
            this.dailyRequests = dailyRequests;
        }

        // Getters
        public long getTotalInputTokens() { return totalInputTokens; }
        public long getTotalOutputTokens() { return totalOutputTokens; }
        public int getTotalRequests() { return totalRequests; }
        public long getDailyInputTokens() { return dailyInputTokens; }
        public long getDailyOutputTokens() { return dailyOutputTokens; }
        public int getDailyRequests() { return dailyRequests; }
        public long getTotalTokens() { return totalInputTokens + totalOutputTokens; }
        public long getDailyTokens() { return dailyInputTokens + dailyOutputTokens; }
    }

    /**
     * Cost estimation
     */
    public static class CostEstimate {
        private final double totalCost;
        private final double dailyCost;
        private final double inputCostPerMillion;
        private final double outputCostPerMillion;

        public CostEstimate(double totalCost, double dailyCost, 
                          double inputCostPerMillion, double outputCostPerMillion) {
            this.totalCost = totalCost;
            this.dailyCost = dailyCost;
            this.inputCostPerMillion = inputCostPerMillion;
            this.outputCostPerMillion = outputCostPerMillion;
        }

        // Getters
        public double getTotalCost() { return totalCost; }
        public double getDailyCost() { return dailyCost; }
        public double getInputCostPerMillion() { return inputCostPerMillion; }
        public double getOutputCostPerMillion() { return outputCostPerMillion; }
    }

    /**
     * User-specific token usage tracking
     */
    public static class UserTokenUsage {
        private final AtomicLong inputTokens = new AtomicLong(0);
        private final AtomicLong outputTokens = new AtomicLong(0);
        private final AtomicInteger requests = new AtomicInteger(0);
        private final LocalDateTime firstRequest = LocalDateTime.now();

        public void recordUsage(int inputTokens, int outputTokens) {
            this.inputTokens.addAndGet(inputTokens);
            this.outputTokens.addAndGet(outputTokens);
            this.requests.incrementAndGet();
        }

        // Getters
        public long getInputTokens() { return inputTokens.get(); }
        public long getOutputTokens() { return outputTokens.get(); }
        public int getRequests() { return requests.get(); }
        public long getTotalTokens() { return inputTokens.get() + outputTokens.get(); }
        public LocalDateTime getFirstRequest() { return firstRequest; }
    }
}
