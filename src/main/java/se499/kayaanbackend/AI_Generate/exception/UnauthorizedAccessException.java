package se499.kayaanbackend.AI_Generate.exception;

/**
 * Exception thrown when a user tries to access a resource they don't have permission for
 */
public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UnauthorizedAccessException(String resourceType, Long resourceId, Long userId) {
        super(String.format("User %d is not authorized to access %s %d", userId, resourceType, resourceId));
    }
    
    public UnauthorizedAccessException(String operation, String resourceType, Long resourceId) {
        super(String.format("Operation '%s' not allowed on %s %d", operation, resourceType, resourceId));
    }
}
