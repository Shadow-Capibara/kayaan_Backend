package se499.kayaanbackend.Study_Streak.exception;

public class StudyStreakException extends RuntimeException {

    public StudyStreakException(String message) {
        super(message);
    }

    public StudyStreakException(String message, Throwable cause) {
        super(message, cause);
    }
} 