package se499.kayaanbackend.AI_Generate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import se499.kayaanbackend.AI_Generate.dto.ApiResponseDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Global Exception Handler for AI Generation feature
 * Provides consistent error responses across all endpoints
 */
@Slf4j
@RestControllerAdvice(basePackages = "se499.kayaanbackend.AI_Generate.controller")
public class GlobalExceptionHandler {

    // ==================== VALIDATION EXCEPTIONS ====================

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponseDTO<Map<String, String>> response = ApiResponseDTO.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleConstraintViolation(
            ConstraintViolationException ex) {
        
        log.warn("Constraint violation: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        
        ApiResponseDTO<Map<String, String>> response = ApiResponseDTO.<Map<String, String>>builder()
                .success(false)
                .message("Constraint validation failed")
                .data(errors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle method argument type mismatch
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        
        log.warn("Type mismatch for parameter '{}': expected {}, got {}", 
                ex.getName(), ex.getRequiredType(), ex.getValue());
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s", 
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Parameter type mismatch")
                .data(message)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle HTTP message not readable exceptions
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        
        log.warn("HTTP message not readable: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Invalid request body format")
                .data("Please check your JSON format and required fields")
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ==================== BUSINESS LOGIC EXCEPTIONS ====================

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Resource not found")
                .data(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle unauthorized access exceptions
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleUnauthorizedAccess(
            UnauthorizedAccessException ex) {
        
        log.warn("Unauthorized access: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Access denied")
                .data(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handle invalid operation exceptions
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleInvalidOperation(
            InvalidOperationException ex) {
        
        log.warn("Invalid operation: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Invalid operation")
                .data(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle rate limiting exceptions
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleRateLimitExceeded(
            RateLimitExceededException ex) {
        
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Rate limit exceeded")
                .data(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    /**
     * Handle AI service exceptions
     */
    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleAIServiceException(
            AIServiceException ex) {
        
        log.error("AI service error: {}", ex.getMessage(), ex);
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("AI service error")
                .data(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    // ==================== SECURITY EXCEPTIONS ====================

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleAuthenticationException(
            AuthenticationException ex) {
        
        log.warn("Authentication failed: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Authentication failed")
                .data("Please provide valid credentials")
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleAccessDenied(
            AccessDeniedException ex) {
        
        log.warn("Access denied: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Access denied")
                .data("You don't have permission to perform this action")
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // ==================== GENERAL EXCEPTIONS ====================

    /**
     * Handle runtime exceptions (business logic errors)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleRuntimeException(
            RuntimeException ex) {
        
        log.error("Runtime error: {}", ex.getMessage(), ex);
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Operation failed")
                .data(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Invalid argument")
                .data(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleIllegalStateException(
            IllegalStateException ex) {
        
        log.warn("Illegal state: {}", ex.getMessage());
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Invalid state")
                .data(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ==================== CATCH-ALL EXCEPTION HANDLER ====================

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<String>> handleGenericException(Exception ex) {
        
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(false)
                .message("Internal server error")
                .data("An unexpected error occurred. Please try again later.")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
