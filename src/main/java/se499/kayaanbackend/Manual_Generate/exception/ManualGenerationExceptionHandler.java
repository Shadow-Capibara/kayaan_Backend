package se499.kayaanbackend.Manual_Generate.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for Manual Generation controllers
 */
@RestControllerAdvice(basePackages = "se499.kayaanbackend.Manual_Generate")
@Slf4j
public class ManualGenerationExceptionHandler {

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "ValidationError");
        response.put("message", "Validation failed");
        response.put("details", errors);
        response.put("success", false);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle HTTP message not readable exceptions
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        
        log.warn("HTTP message not readable: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "InvalidRequestFormat");
        response.put("message", "Invalid request body format");
        response.put("details", "Please check your JSON format and required fields");
        response.put("success", false);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ManualGenerationException.class)
    public ResponseEntity<Map<String, Object>> handleManualGenerationException(ManualGenerationException e) {
        log.error("Manual Generation exception: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "ManualGenerationError");
        response.put("message", e.getMessage());
        response.put("success", false);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ContentValidationException.class)
    public ResponseEntity<Map<String, Object>> handleContentValidationException(ContentValidationException e) {
        log.error("Content validation exception: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "ContentValidationError");
        response.put("message", e.getMessage());
        response.put("details", "Please check your content structure and format");
        response.put("success", false);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception in Manual Generation: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "RuntimeException");
        response.put("message", e.getMessage());
        response.put("success", false);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Invalid argument in Manual Generation: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "InvalidArgument");
        response.put("message", e.getMessage());
        response.put("details", "Please check your input parameters");
        response.put("success", false);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected exception in Manual Generation: ", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "InternalServerError");
        response.put("message", "An unexpected error occurred: " + e.getMessage());
        response.put("success", false);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
