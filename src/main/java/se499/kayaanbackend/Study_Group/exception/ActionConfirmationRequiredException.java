package se499.kayaanbackend.Study_Group.exception;

/**
 * Exception สำหรับการกระทำที่ต้องการการยืนยัน
 */
public class ActionConfirmationRequiredException extends SecurityException {
    
    public static final String ERROR_CODE = "ACTION_CONFIRMATION_REQUIRED";
    public static final int HTTP_STATUS = 428; // Precondition Required
    
    private final String confirmationToken;
    
    public ActionConfirmationRequiredException(String message, String confirmationToken) {
        super(message, ERROR_CODE, HTTP_STATUS);
        this.confirmationToken = confirmationToken;
    }
    
    public ActionConfirmationRequiredException(String message, String confirmationToken, Throwable cause) {
        super(message, ERROR_CODE, HTTP_STATUS, cause);
        this.confirmationToken = confirmationToken;
    }
    
    public String getConfirmationToken() {
        return confirmationToken;
    }
}
