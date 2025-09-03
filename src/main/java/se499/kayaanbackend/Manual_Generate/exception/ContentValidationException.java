package se499.kayaanbackend.Manual_Generate.exception;

/**
 * Exception thrown when content validation fails
 */
public class ContentValidationException extends RuntimeException {
    
    public ContentValidationException(String message) {
        super(message);
    }
    
    public ContentValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
