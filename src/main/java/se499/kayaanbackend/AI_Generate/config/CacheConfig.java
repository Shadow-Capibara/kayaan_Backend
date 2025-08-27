package se499.kayaanbackend.AI_Generate.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for AI generation responses
 * Reduces API calls and saves costs by caching similar requests
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Caffeine cache manager for AI responses
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
            // Cache size: 1000 entries
            .maximumSize(1000)
            // Expire after 1 hour
            .expireAfterWrite(1, TimeUnit.HOURS)
            // Expire after 30 minutes of no access
            .expireAfterAccess(30, TimeUnit.MINUTES)
            // Record stats for monitoring
            .recordStats()
        );
        
        return cacheManager;
    }

    /**
     * Cache names for different types of AI content
     */
    public static final class CacheNames {
        public static final String AI_FLASHCARD = "ai-flashcard";
        public static final String AI_QUIZ = "ai-quiz";
        public static final String AI_NOTE = "ai-note";
        public static final String AI_SUMMARY = "ai-summary";
        public static final String AI_GENERAL = "ai-general";
        
        private CacheNames() {}
    }
}
