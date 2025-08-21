package se499.kayaanbackend.Study_Group.exception;

/**
 * Exception สำหรับการเข้าถึงเนื้อหาที่ถูกปฏิเสธ
 */
public class ContentAccessDeniedException extends SecurityException {
    
    public static final String ERROR_CODE = "CONTENT_ACCESS_DENIED";
    public static final int HTTP_STATUS = 403;
    
    public ContentAccessDeniedException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }
    
    public ContentAccessDeniedException(String message, Throwable cause) {
        super(message, ERROR_CODE, HTTP_STATUS, cause);
    }
}
