package se499.kayaanbackend.AI_Generate.entity;

/**
 * Enum for supported content types in AI Generation feature
 * Only 3 types are supported: FLASHCARD, QUIZ, NOTE
 */
public enum ContentType {
    FLASHCARD("flashcard"),
    QUIZ("quiz"),
    NOTE("note");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType fromString(String text) {
        for (ContentType type : ContentType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No content type with value " + text + " found");
    }

    public static boolean isValid(String text) {
        for (ContentType type : ContentType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
