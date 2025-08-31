package se499.kayaanbackend.AI_Generate.exception;

/**
 * Exception thrown when an operation is not valid for the current state
 */
public class InvalidOperationException extends RuntimeException {
    
    public InvalidOperationException(String message) {
        super(message);
    }
    
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidOperationException(String operation, String currentState) {
        super(String.format("Operation '%s' is not valid in current state: %s", operation, currentState));
    }
    
    public InvalidOperationException(String operation, String resourceType, Long resourceId, String reason) {
        super(String.format("Operation '%s' on %s %d is invalid: %s", operation, resourceType, resourceId, reason));
    }
}
