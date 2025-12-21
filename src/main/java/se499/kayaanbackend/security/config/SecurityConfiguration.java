package se499.kayaanbackend.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .headers(headers -> headers.frameOptions().disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/auth/**", "/api/auth/**").permitAll()
                        .requestMatchers("/authenticate", "/register").permitAll() // Authentication endpoints
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // เผื่อ preflight
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/ai/config/test").permitAll() // AI config test endpoint
                        .requestMatchers("/api/ai/debug/**").permitAll() // AI debug endpoints

                        // Theme endpoints
                        .requestMatchers(HttpMethod.GET, "/api/themes").permitAll()

                        // User endpoints
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/*/avatar-upload-url").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/avatar/upload-proxy").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/*/avatar-upload-proxy").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/avatar-url").authenticated()
                        .requestMatchers("/api/users/*/**").authenticated()

                        // Manual Generation endpoints
                        .requestMatchers("/api/quiz/**").authenticated()
                        .requestMatchers("/api/note/**").authenticated()
                        .requestMatchers("/api/flashcard/**").authenticated()
                        .requestMatchers("/api/content/manual/test").permitAll() // Test endpoint for validation
                        .requestMatchers("/api/content/manual/**").authenticated() // Manual content API
                        .requestMatchers("/api/content/**").authenticated() // Other content API

                        // Study Group endpoints - ต้องมี authentication
                        .requestMatchers("/api/groups/**").authenticated()
                        .requestMatchers("/api/study-groups/**").authenticated()
                        .requestMatchers("/api/group-members/**").authenticated()
                        .requestMatchers("/api/group-content/**").authenticated()
                        .requestMatchers("/api/group-messages/**").authenticated()
                        .requestMatchers("/api/group-invites/**").authenticated()

                        // Admin endpoints - ต้องมี role ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // อื่นๆ ต้องมี authentication
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(accessDeniedHandler))
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(
                                (request, response, authentication) -> SecurityContextHolder.clearContext()));
        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // อนุญาต origin จาก frontend
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000", // React dev server
                "http://localhost:3001", // Alternative port
                "http://localhost:5173", // Vite dev server
                "http://localhost:5174", // Vite dev server (alternative port)
                "https://kayaan-frontend.vercel.app", // Production frontend
                "https://kayaan-project-4jbs.vercel.app", // Current Vercel deployment
                "https://*.vercel.app" // Vercel deployments
        ));

        // อนุญาต HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // อนุญาต headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With",
                "Accept", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));

        // อนุญาต credentials
        configuration.setAllowCredentials(true);

        // ตั้งค่า max age
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/v1/auth/") ||
                requestURI.startsWith("/api/auth/") ||
                requestURI.equals("/authenticate") ||
                requestURI.equals("/register") ||
                requestURI.startsWith("/api/public/") ||
                requestURI.equals("/api/ai/config/test") ||
                requestURI.startsWith("/api/ai/debug/") ||
                requestURI.equals("/api/themes");
    }
}
