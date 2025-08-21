package se499.kayaanbackend.Study_Group.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import se499.kayaanbackend.security.config.JwtAuthenticationFilter;

import java.util.Arrays;

/**
 * Configuration สำหรับ security ของ Study Group
 */
@Configuration
@EnableWebSecurity
public class GroupSecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    public GroupSecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    
    @Bean
    public SecurityFilterChain groupSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(groupCorsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // Study Group endpoints - ต้องมี authentication
                .requestMatchers("/api/study-groups/**").authenticated()
                .requestMatchers("/api/group-members/**").authenticated()
                .requestMatchers("/api/group-content/**").authenticated()
                .requestMatchers("/api/group-messages/**").authenticated()
                .requestMatchers("/api/group-invites/**").authenticated()
                
                // Admin endpoints - ต้องมี role ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // อื่นๆ ต้องมี authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean(name = "groupCorsConfigurationSource")
    public CorsConfigurationSource groupCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // อนุญาต origin จาก frontend
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",      // React dev server
            "http://localhost:3001",      // Alternative port
            "https://kayaan-frontend.vercel.app", // Production frontend
            "https://*.vercel.app"       // Vercel deployments
        ));
        
        // อนุญาต HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // อนุญาต headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // อนุญาต credentials
        configuration.setAllowCredentials(true);
        
        // ตั้งค่า max age
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
