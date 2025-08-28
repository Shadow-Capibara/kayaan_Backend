package se499.kayaanbackend.AI_Generate.exception;

/**
 * Exception thrown when an unsupported content type is provided
 * Only FLASHCARD, QUIZ, and NOTE are supported
 */
public class InvalidContentTypeException extends RuntimeException {

    private final String invalidContentType;
    private final String[] supportedTypes;

    public InvalidContentTypeException(String invalidContentType, String[] supportedTypes) {
        super(String.format("Unsupported content type: '%s'. Supported types: %s",
            invalidContentType, String.join(", ", supportedTypes)));
        this.invalidContentType = invalidContentType;
        this.supportedTypes = supportedTypes;
    }

    public InvalidContentTypeException(String message) {
        super(message);
        this.invalidContentType = null;
        this.supportedTypes = null;
    }

    public String getInvalidContentType() {
        return invalidContentType;
    }

    public String[] getSupportedTypes() {
        return supportedTypes;
    }
}
