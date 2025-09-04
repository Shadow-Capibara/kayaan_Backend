package se499.kayaanbackend.Study_Group.exception;

public class StudyGroupException extends RuntimeException {
    private final int statusCode;
    
    public StudyGroupException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public StudyGroupException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}