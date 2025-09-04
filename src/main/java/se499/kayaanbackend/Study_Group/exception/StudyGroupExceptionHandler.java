package se499.kayaanbackend.Study_Group.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "se499.kayaanbackend.Study_Group")
public class StudyGroupExceptionHandler {
    
    @ExceptionHandler(StudyGroupException.class)
    public ResponseEntity<Map<String, Object>> handleStudyGroupException(StudyGroupException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("error", "StudyGroupException");
        errorResponse.put("status", ex.getStatusCode());
        errorResponse.put("timestamp", LocalDateTime.now());
        
        HttpStatus httpStatus = HttpStatus.valueOf(ex.getStatusCode());
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Authentication failed");
        errorResponse.put("error", "AuthenticationException");
        errorResponse.put("status", 401);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("details", "Please provide valid JWT token in Authorization header");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Access denied");
        errorResponse.put("error", "AccessDeniedException");
        errorResponse.put("status", 403);
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("details", "You don't have permission to perform this action");
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("error", "RuntimeException");
        errorResponse.put("status", 400);
        errorResponse.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}