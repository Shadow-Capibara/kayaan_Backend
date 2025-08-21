package se499.kayaanbackend.Study_Group.exception;

/**
 * Exception สำหรับรหัสเชิญที่ไม่ถูกต้อง
 */
public class InvalidInviteCodeException extends SecurityException {
    
    public static final String ERROR_CODE = "INVALID_INVITE_CODE";
    public static final int HTTP_STATUS = 400;
    
    public InvalidInviteCodeException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }
    
    public InvalidInviteCodeException(String message, Throwable cause) {
        super(message, ERROR_CODE, HTTP_STATUS, cause);
    }
}
