package se499.kayaanbackend.Study_Group.exception;

import java.time.LocalDateTime;

/**
 * Exception สำหรับการใช้งานเกินขีดจำกัด
 */
public class RateLimitExceededException extends SecurityException {
    
    public static final String ERROR_CODE = "RATE_LIMIT_EXCEEDED";
    public static final int HTTP_STATUS = 429; // Too Many Requests
    
    private final LocalDateTime resetTime;
    private final String actionType;
    
    public RateLimitExceededException(String message, String actionType, LocalDateTime resetTime) {
        super(message, ERROR_CODE, HTTP_STATUS);
        this.actionType = actionType;
        this.resetTime = resetTime;
    }
    
    public RateLimitExceededException(String message, String actionType, LocalDateTime resetTime, Throwable cause) {
        super(message, ERROR_CODE, HTTP_STATUS, cause);
        this.actionType = actionType;
        this.resetTime = resetTime;
    }
    
    public LocalDateTime getResetTime() {
        return resetTime;
    }
    
    public String getActionType() {
        return actionType;
    }
}
