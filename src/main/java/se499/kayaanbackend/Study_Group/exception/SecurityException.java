package se499.kayaanbackend.Study_Group.exception;

/**
 * Base exception class สำหรับ security exceptions ในระบบ Study Group
 */
public abstract class SecurityException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    
    public SecurityException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public SecurityException(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
}
