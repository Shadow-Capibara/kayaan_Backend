package se499.kayaanbackend.AI_Generate.exception;

/**
 * Exception thrown when rate limits are exceeded
 */
public class RateLimitExceededException extends RuntimeException {
    
    private final String limitType;
    private final int limit;
    private final String timeWindow;
    
    public RateLimitExceededException(String message) {
        super(message);
        this.limitType = "unknown";
        this.limit = 0;
        this.timeWindow = "unknown";
    }
    
    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
        this.limitType = "unknown";
        this.limit = 0;
        this.timeWindow = "unknown";
    }
    
    public RateLimitExceededException(String limitType, int limit, String timeWindow) {
        super(String.format("Rate limit exceeded: %d %s per %s", limit, limitType, timeWindow));
        this.limitType = limitType;
        this.limit = limit;
        this.timeWindow = timeWindow;
    }
    
    public RateLimitExceededException(String limitType, int limit, String timeWindow, String message) {
        super(message);
        this.limitType = limitType;
        this.limit = limit;
        this.timeWindow = timeWindow;
    }
    
    public String getLimitType() {
        return limitType;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public String getTimeWindow() {
        return timeWindow;
    }
}
