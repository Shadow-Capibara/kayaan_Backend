package se499.kayaanbackend.Manual_Generate.exception;

/**
 * Custom exception for Manual Generation operations
 */
public class ManualGenerationException extends RuntimeException {
    
    public ManualGenerationException(String message) {
        super(message);
    }
    
    public ManualGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
