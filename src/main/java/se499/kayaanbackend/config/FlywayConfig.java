package se499.kayaanbackend.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Flyway configuration to handle migration issues
 * This configuration will attempt to repair Flyway schema before migration
 */
@Configuration
public class FlywayConfig {

    @Bean
    @Primary
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway, (f) -> {
            try {
                // Try to repair first
                f.repair();
                // Then migrate
                f.migrate();
            } catch (Exception e) {
                // If repair fails, try baseline and migrate
                try {
                    f.baseline();
                    f.migrate();
                } catch (Exception ex) {
                    // Log error but don't fail startup
                    System.err.println("Flyway migration failed: " + ex.getMessage());
                    System.err.println("Application will continue without migration");
                }
            }
        });
    }
}
