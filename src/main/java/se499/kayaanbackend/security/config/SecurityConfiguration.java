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

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

      http
              .headers(headers -> headers.frameOptions().disable())
              .cors(cors -> cors.configurationSource(corsConfigurationSource()))
              .csrf(csrf -> csrf.disable())
              .authorizeHttpRequests(auth -> auth
                      // Public endpoints
                      .requestMatchers("/api/v1/auth/**", "/api/auth/**").permitAll()
                      .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // เผื่อ preflight
                      .requestMatchers("/api/public/**").permitAll()
                      
                      // Theme endpoints
                      .requestMatchers(HttpMethod.GET, "/api/themes").permitAll()
                      
                      // User endpoints
                      .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                      .requestMatchers(HttpMethod.POST, "/api/users/*/avatar-upload-url").authenticated()
                      .requestMatchers(HttpMethod.POST, "/api/avatar/upload-proxy").authenticated()
                      .requestMatchers(HttpMethod.POST, "/api/users/*/avatar-upload-proxy").authenticated()
                      .requestMatchers(HttpMethod.PUT, "/api/users/*/avatar-url").authenticated()
                      .requestMatchers("/api/users/*/**").authenticated()
                      
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
                      .anyRequest().authenticated()
              )
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authenticationProvider(authenticationProvider)
              .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
              .logout(logout -> logout
                      .logoutUrl("/api/v1/auth/logout")
                      .addLogoutHandler(logoutHandler)
                      .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
              );
      return http.build();

  }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // อนุญาต origin จาก frontend
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",      // React dev server
            "http://localhost:3001",      // Alternative port
            "http://localhost:5173",      // Vite dev server
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
