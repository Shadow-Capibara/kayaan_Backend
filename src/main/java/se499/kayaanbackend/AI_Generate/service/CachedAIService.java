package se499.kayaanbackend.AI_Generate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Cached AI service that provides intelligent caching for AI generation requests
 * Reduces API calls and saves costs by caching similar requests
 */
@Slf4j
@Service
public class CachedAIService {

    @Autowired
    private OpenAIService openAIService;

    /**
     * Generate content with intelligent caching
     * @param prompt User prompt
     * @param outputFormat Expected output format
     * @param additionalContext Additional context
     * @param userId User ID for tracking
     * @return Generated content
     */
    @Cacheable(value = "ai-content", key = "#generateCacheKey(#prompt, #outputFormat, #additionalContext)")
    public String generateContentWithCache(String prompt, String outputFormat, String additionalContext, String userId) {
        log.info("Cache miss - generating new content for format: {}, user: {}", outputFormat, userId);
        return openAIService.generateContent(prompt, outputFormat, additionalContext, userId);
    }

    /**
     * Generate content with cache (backward compatibility)
     */
    @Cacheable(value = "ai-content", key = "#generateCacheKey(#prompt, #outputFormat, #additionalContext)")
    public String generateContentWithCache(String prompt, String outputFormat, String additionalContext) {
        return generateContentWithCache(prompt, outputFormat, additionalContext, "anonymous");
    }

    /**
     * Generate cache key based on input parameters
     * Uses SHA-256 hash for consistent and short cache keys
     */
    private String generateCacheKey(String prompt, String outputFormat, String additionalContext) {
        try {
            String combinedInput = String.format("%s|%s|%s", 
                prompt.toLowerCase().trim(), 
                outputFormat.toLowerCase(), 
                additionalContext != null ? additionalContext.toLowerCase().trim() : "");
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combinedInput.getBytes(StandardCharsets.UTF_8));
            
            // Use first 16 characters of base64 hash for shorter cache keys
            return Base64.getEncoder().encodeToString(hash).substring(0, 16);
            
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate cache key", e);
            // Fallback to simple hash
            return String.format("%s-%s-%s", 
                prompt.hashCode(), 
                outputFormat.hashCode(), 
                additionalContext != null ? additionalContext.hashCode() : 0);
        }
    }

    /**
     * Check if content exists in cache without generating
     * @param prompt User prompt
     * @param outputFormat Expected output format
     * @param additionalContext Additional context
     * @return True if content exists in cache
     */
    public boolean isContentCached(String prompt, String outputFormat, String additionalContext) {
        String cacheKey = generateCacheKey(prompt, outputFormat, additionalContext);
        log.debug("Checking cache for key: {}", cacheKey);
        
        // This is a simplified check - in practice, you might want to use a more sophisticated approach
        // For now, we'll rely on Spring's cache abstraction
        return false; // Placeholder - actual implementation would check cache directly
    }

    /**
     * Get cache statistics
     * @return Cache statistics string
     */
    public String getCacheStats() {
        // This would return actual cache statistics from Caffeine
        // For now, returning placeholder
        return "Cache statistics not available";
    }
}
