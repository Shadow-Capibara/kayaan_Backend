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
import org.springframework.core.env.Environment;
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
  private final Environment environment;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

      http
              .headers(headers -> headers.frameOptions().disable())
              .cors(cors -> cors.configurationSource(corsConfigurationSource()))
              .csrf(csrf -> csrf.disable())
//              .authorizeHttpRequests(auth -> auth
//                      .requestMatchers("/api/v1/auth/**").permitAll()
//                      .requestMatchers(HttpMethod.POST,
//                              "/api/users/*/avatar-upload").hasRole("USER")
//                      .requestMatchers(HttpMethod.PUT ,
//                              "/api/users/*/avatar-url").hasRole("USER")
//                      .anyRequest().authenticated()
//              )
.authorizeHttpRequests(auth -> auth
.requestMatchers("/api/v1/auth/**", "/api/auth/**").permitAll()
.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // เผื่อ preflight

.requestMatchers(HttpMethod.GET,  "/api/themes").permitAll()
.requestMatchers(HttpMethod.GET,  "/api/users/me").authenticated()

// ใช้ ant pattern (*) แทน {id}
.requestMatchers(HttpMethod.POST, "/api/users/*/avatar-upload-url").authenticated()
.requestMatchers(HttpMethod.POST, "/api/avatar/upload-proxy").authenticated()
.requestMatchers(HttpMethod.POST, "/api/users/*/avatar-upload-proxy").authenticated()
.requestMatchers(HttpMethod.PUT,  "/api/users/*/avatar-url").authenticated()

// ถ้าคุณมีเส้นอื่นใต้ /api/users/{id}/... แล้วต้องการบังคับ auth:
.requestMatchers("/api/users/*/**").authenticated()

// Study Group endpoints - require authentication
.requestMatchers("/api/groups/**").authenticated()

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
        var config = new CorsConfiguration();
        String origins = environment.getProperty("kayaan.cors.allowed-origins");
        if (origins == null || origins.isBlank()) {
            origins = System.getProperty("CORS_ALLOWED_ORIGINS", System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", "http://localhost:5173"));
        }
        config.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}
