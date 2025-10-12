package se499.kayaanbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for enabling scheduled tasks
 * Required for Study Streak daily check jobs
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Configuration for scheduled tasks
    // This enables @Scheduled annotations throughout the application
}
