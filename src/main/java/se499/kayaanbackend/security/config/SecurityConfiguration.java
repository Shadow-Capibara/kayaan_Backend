package se499.kayaanbackend.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
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

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

      http
              .headers(headers -> headers.frameOptions().disable())
              .cors(cors -> cors.configurationSource(request -> {
                  CorsConfiguration config = new CorsConfiguration();
                  config.setAllowedOrigins(List.of("http://localhost:5173"));
                  config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                  config.setAllowedHeaders(List.of("*"));
                  config.setAllowCredentials(true);
                  return config;
              }))
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
                      .requestMatchers("/api/v1/auth/**").permitAll()
                      .requestMatchers(HttpMethod.GET, "/api/themes").permitAll()
                      .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                      .requestMatchers(HttpMethod.POST, "/api/users/{id}/avatar-upload").authenticated()
                      .requestMatchers(HttpMethod.POST, "/api/users/{id}/avatar-upload-url").authenticated()
                      .requestMatchers(HttpMethod.POST, "/api/avatar/upload-proxy").authenticated()
                      .requestMatchers(HttpMethod.PUT, "/api/users/{id}/avatar-url").authenticated()
                      .requestMatchers("/api/users/{id}/**").authenticated()
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
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}
