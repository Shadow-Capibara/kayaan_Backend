package se499.kayaanbackend.AI_Generate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import se499.kayaanbackend.AI_Generate.service.AIGenerationRateLimitService;
import se499.kayaanbackend.AI_Generate.service.TokenUsageService;
import se499.kayaanbackend.AI_Generate.service.CachedAIService;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for monitoring AI generation usage, costs, and rate limits
 * Provides endpoints for cost optimization and monitoring
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/monitoring")
public class AIMonitoringController {

    @Autowired
    private AIGenerationRateLimitService rateLimitService;

    @Autowired
    private TokenUsageService tokenUsageService;

    @Autowired
    private CachedAIService cachedAIService;

    /**
     * Get current rate limit usage for a user
     */
    @GetMapping("/rate-limits/{userId}")
    public ResponseEntity<Map<String, Object>> getRateLimitUsage(@PathVariable String userId) {
        try {
            AIGenerationRateLimitService.RateLimitInfo usage = rateLimitService.getCurrentUsage(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("daily", Map.of(
                "used", usage.getDailyUsed(),
                "limit", usage.getDailyLimit(),
                "remaining", usage.getDailyRemaining()
            ));
            response.put("hourly", Map.of(
                "used", usage.getHourlyUsed(),
                "limit", usage.getHourlyLimit(),
                "remaining", usage.getHourlyRemaining()
            ));
            response.put("minute", Map.of(
                "used", usage.getMinuteUsed(),
                "limit", usage.getMinuteLimit(),
                "remaining", usage.getMinuteRemaining()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get rate limit usage for user: {}", userId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get rate limit usage"));
        }
    }

    /**
     * Get token usage statistics
     */
    @GetMapping("/token-usage")
    public ResponseEntity<Map<String, Object>> getTokenUsage() {
        try {
            TokenUsageService.TokenUsageStats stats = tokenUsageService.getCurrentUsage();
            TokenUsageService.CostEstimate cost = tokenUsageService.estimateCost();
            double monthlyProjection = tokenUsageService.getMonthlyCostProjection();
            
            Map<String, Object> response = new HashMap<>();
            response.put("tokens", Map.of(
                "totalInput", stats.getTotalInputTokens(),
                "totalOutput", stats.getTotalOutputTokens(),
                "total", stats.getTotalTokens(),
                "dailyInput", stats.getDailyInputTokens(),
                "dailyOutput", stats.getDailyOutputTokens(),
                "daily", stats.getDailyTokens()
            ));
            response.put("requests", Map.of(
                "total", stats.getTotalRequests(),
                "daily", stats.getDailyRequests()
            ));
            response.put("costs", Map.of(
                "total", String.format("$%.4f", cost.getTotalCost()),
                "daily", String.format("$%.4f", cost.getDailyCost()),
                "monthlyProjection", String.format("$%.4f", monthlyProjection),
                "inputCostPerMillion", String.format("$%.3f", cost.getInputCostPerMillion()),
                "outputCostPerMillion", String.format("$%.3f", cost.getOutputCostPerMillion())
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get token usage statistics", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get token usage statistics"));
        }
    }

    /**
     * Get user-specific token usage
     */
    @GetMapping("/token-usage/{userId}")
    public ResponseEntity<Map<String, Object>> getUserTokenUsage(@PathVariable String userId) {
        try {
            TokenUsageService.UserTokenUsage usage = tokenUsageService.getUserUsage(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("tokens", Map.of(
                "input", usage.getInputTokens(),
                "output", usage.getOutputTokens(),
                "total", usage.getTotalTokens()
            ));
            response.put("requests", usage.getRequests());
            response.put("firstRequest", usage.getFirstRequest().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get token usage for user: {}", userId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get user token usage"));
        }
    }

    /**
     * Get cache statistics
     */
    @GetMapping("/cache-stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        try {
            String cacheStats = cachedAIService.getCacheStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("cacheStats", cacheStats);
            response.put("status", "active");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get cache statistics", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get cache statistics"));
        }
    }

    /**
     * Reset rate limits for a user (admin only)
     */
    @PostMapping("/rate-limits/{userId}/reset")
    public ResponseEntity<Map<String, Object>> resetRateLimits(@PathVariable String userId) {
        try {
            rateLimitService.resetRateLimits(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rate limits reset successfully");
            response.put("userId", userId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to reset rate limits for user: {}", userId, e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to reset rate limits"));
        }
    }

    /**
     * Reset all token usage counters (admin only)
     */
    @PostMapping("/token-usage/reset")
    public ResponseEntity<Map<String, Object>> resetTokenUsage() {
        try {
            tokenUsageService.resetAllCounters();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Token usage counters reset successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to reset token usage counters", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to reset token usage counters"));
        }
    }

    /**
     * Get cost optimization recommendations
     */
    @GetMapping("/cost-optimization")
    public ResponseEntity<Map<String, Object>> getCostOptimizationTips() {
        try {
            TokenUsageService.TokenUsageStats stats = tokenUsageService.getCurrentUsage();
            TokenUsageService.CostEstimate cost = tokenUsageService.estimateCost();
            
            Map<String, Object> recommendations = new HashMap<>();
            
            // Analyze usage patterns and provide recommendations
            if (stats.getDailyTokens() > 10000) {
                recommendations.put("highUsage", "Consider implementing more aggressive caching");
            }
            
            if (cost.getDailyCost() > 0.10) {
                recommendations.put("highCost", "Daily cost is high - review prompt optimization");
            }
            
            if (stats.getDailyRequests() > 20) {
                recommendations.put("manyRequests", "High request volume - consider batch processing for similar requests");
            }
            
            recommendations.put("general", "Use GPT-5 Nano model for best cost efficiency");
            recommendations.put("caching", "Enable caching to reduce duplicate API calls");
            recommendations.put("promptOptimization", "Keep prompts concise and specific");
            
            Map<String, Object> response = new HashMap<>();
            response.put("recommendations", recommendations);
            response.put("currentDailyCost", String.format("$%.4f", cost.getDailyCost()));
            response.put("currentDailyTokens", stats.getDailyTokens());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get cost optimization tips", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get cost optimization tips"));
        }
    }
}
