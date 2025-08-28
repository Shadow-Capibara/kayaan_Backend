package se499.kayaanbackend.AI_Generate.service;

import org.springframework.stereotype.Service;

import se499.kayaanbackend.AI_Generate.entity.ContentType;

/**
 * Service for validating content types in AI Generation feature
 * Ensures only supported content types are used: FLASHCARD, QUIZ, NOTE
 */
@Service
public class ContentTypeValidationService {
    
    /**
     * Validate if the given content type string is supported
     * @param contentType Content type string to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            return false;
        }
        return ContentType.isValid(contentType.trim());
    }
    
    /**
     * Convert string to ContentType enum
     * @param contentType Content type string
     * @return ContentType enum
     * @throws IllegalArgumentException if content type is not supported
     */
    public ContentType toContentType(String contentType) {
        if (!isValidContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported content type: " + contentType + 
                ". Supported types: flashcard, quiz, note");
        }
        return ContentType.fromString(contentType.trim());
    }
    
    /**
     * Get all supported content types as strings
     * @return Array of supported content type strings
     */
    public String[] getSupportedContentTypes() {
        ContentType[] types = ContentType.values();
        String[] result = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            result[i] = types[i].getValue();
        }
        return result;
    }
    
    /**
     * Get validation error message for unsupported content type
     * @param contentType Invalid content type
     * @return Error message
     */
    public String getValidationErrorMessage(String contentType) {
        return String.format("Unsupported content type: '%s'. Supported types: %s", 
            contentType, String.join(", ", getSupportedContentTypes()));
    }
}
