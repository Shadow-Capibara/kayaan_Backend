package se499.kayaanbackend.AI_Generate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class for AI Generation feature
 */
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "ai.generation")
@Data
public class AIGenerationConfig {

    // Rate limiting configuration
    private RateLimit rateLimit = new RateLimit();
    
    // Async processing configuration
    private Async async = new Async();
    
    // OpenAI configuration
    private OpenAI openAI = new OpenAI();
    
    // Supabase configuration
    private Supabase supabase = new Supabase();

    @Data
    public static class RateLimit {
        private int maxRequestsPerHour = 5;
        private int maxPreviewsPerMinute = 3;
        private int maxTemplatesPerUser = 50;
        private int maxContentPerUser = 100;
    }

    @Data
    public static class Async {
        private int corePoolSize = 2;
        private int maxPoolSize = 5;
        private int queueCapacity = 100;
        private String threadNamePrefix = "ai-generation-";
    }

    @Data
    public static class OpenAI {
        private String apiKey;
        private String model = "gpt-3.5-turbo";
        private int maxTokens = 2000;
        private double temperature = 0.7;
        private int timeoutSeconds = 30;
    }

    @Data
    public static class Supabase {
        private String url;
        private String key;
        private String bucket = "ai-generated-content";
        private int maxFileSizeMB = 10;
        private int signedUrlExpiryMinutes = 60;
    }

    /**
     * Configure async executor for AI generation tasks
     */
    @Bean(name = "aiGenerationTaskExecutor")
    public Executor aiGenerationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(async.getCorePoolSize());
        executor.setMaxPoolSize(async.getMaxPoolSize());
        executor.setQueueCapacity(async.getQueueCapacity());
        executor.setThreadNamePrefix(async.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
