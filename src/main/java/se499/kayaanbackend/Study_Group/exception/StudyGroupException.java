package se499.kayaanbackend.Study_Group.exception;

public class StudyGroupException extends RuntimeException {
    
    public StudyGroupException(String message) {
        super(message);
    }
    
    public StudyGroupException(String message, Throwable cause) {
        super(message, cause);
    }
}
