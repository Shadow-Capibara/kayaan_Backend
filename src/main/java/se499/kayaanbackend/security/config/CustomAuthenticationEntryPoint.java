package se499.kayaanbackend.security.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        String requestURI = request.getRequestURI();
        
        // Skip custom error response for public endpoints
        if (isPublicEndpoint(requestURI)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        
        log.warn("Authentication failed for request: {} {}", request.getMethod(), requestURI);
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Authentication failed");
        errorResponse.put("error", "AuthenticationException");
        errorResponse.put("status", 401);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("details", "Please provide valid JWT token in Authorization header");
        errorResponse.put("path", requestURI);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
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
