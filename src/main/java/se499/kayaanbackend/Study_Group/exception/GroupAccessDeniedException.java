package se499.kayaanbackend.Study_Group.exception;

/**
 * Exception สำหรับการเข้าถึงกลุ่มที่ถูกปฏิเสธ
 */
public class GroupAccessDeniedException extends SecurityException {
    
    public static final String ERROR_CODE = "GROUP_ACCESS_DENIED";
    public static final int HTTP_STATUS = 403;
    
    public GroupAccessDeniedException(String message) {
        super(message, ERROR_CODE, HTTP_STATUS);
    }
    
    public GroupAccessDeniedException(String message, Throwable cause) {
        super(message, ERROR_CODE, HTTP_STATUS, cause);
    }
}
