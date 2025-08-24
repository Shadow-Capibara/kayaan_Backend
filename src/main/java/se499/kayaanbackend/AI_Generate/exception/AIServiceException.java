package se499.kayaanbackend.AI_Generate.exception;

/**
 * Exception thrown when AI service operations fail
 */
public class AIServiceException extends RuntimeException {
    
    private final String serviceName;
    private final String operation;
    
    public AIServiceException(String message) {
        super(message);
        this.serviceName = "unknown";
        this.operation = "unknown";
    }
    
    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
        this.serviceName = "unknown";
        this.operation = "unknown";
    }
    
    public AIServiceException(String serviceName, String operation, String message) {
        super(String.format("AI service '%s' failed during '%s': %s", serviceName, operation, message));
        this.serviceName = serviceName;
        this.operation = operation;
    }
    
    public AIServiceException(String serviceName, String operation, String message, Throwable cause) {
        super(String.format("AI service '%s' failed during '%s': %s", serviceName, operation, message), cause);
        this.serviceName = serviceName;
        this.operation = operation;
    }
    
    public AIServiceException(String serviceName, String operation, Throwable cause) {
        super(String.format("AI service '%s' failed during '%s'", serviceName, operation), cause);
        this.serviceName = serviceName;
        this.operation = operation;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getOperation() {
        return operation;
    }
}
