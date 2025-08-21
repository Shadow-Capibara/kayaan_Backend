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

    // Security Exceptions
    @ExceptionHandler(GroupAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleGroupAccessDenied(GroupAccessDeniedException e) {
        Map<String, Object> response = Map.of(
            "error", "GroupAccessDeniedException",
            "errorCode", e.getErrorCode(),
            "message", e.getMessage(),
            "httpStatus", e.getHttpStatus()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(ContentAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleContentAccessDenied(ContentAccessDeniedException e) {
        Map<String, Object> response = Map.of(
            "error", "ContentAccessDeniedException",
            "errorCode", e.getErrorCode(),
            "message", e.getMessage(),
            "httpStatus", e.getHttpStatus()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(InvalidInviteCodeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidInviteCode(InvalidInviteCodeException e) {
        Map<String, Object> response = Map.of(
            "error", "InvalidInviteCodeException",
            "errorCode", e.getErrorCode(),
            "message", e.getMessage(),
            "httpStatus", e.getHttpStatus()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(ActionConfirmationRequiredException.class)
    public ResponseEntity<Map<String, Object>> handleActionConfirmationRequired(ActionConfirmationRequiredException e) {
        Map<String, Object> response = Map.of(
            "error", "ActionConfirmationRequiredException",
            "errorCode", e.getErrorCode(),
            "message", e.getMessage(),
            "httpStatus", e.getHttpStatus(),
            "confirmationToken", e.getConfirmationToken()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(RateLimitExceededException e) {
        Map<String, Object> response = Map.of(
            "error", "RateLimitExceededException",
            "errorCode", e.getErrorCode(),
            "message", e.getMessage(),
            "httpStatus", e.getHttpStatus(),
            "actionType", e.getActionType(),
            "resetTime", e.getResetTime()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
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
