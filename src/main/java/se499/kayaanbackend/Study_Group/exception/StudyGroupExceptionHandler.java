package se499.kayaanbackend.Study_Group.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StudyGroupExceptionHandler {

    @ExceptionHandler(StudyGroupException.class)
    public ResponseEntity<Map<String, String>> handleStudyGroupException(StudyGroupException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "StudyGroupException", "message", e.getMessage()));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConstraint(Exception e) {
        return Map.of("error", "constraint", 
                     "message", String.valueOf(e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleAny(Exception e) {
        return Map.of("error", e.getClass().getSimpleName(), 
                     "message", String.valueOf(e.getMessage()));
    }
}
