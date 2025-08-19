package se499.kayaanbackend.Study_Group.service;

import java.util.Locale;

public final class ResourceValidationUtil {

    private ResourceValidationUtil() {}

    public static String normalizeContentType(String contentType) {
        if (contentType == null) return "application/octet-stream";
        String lower = contentType.toLowerCase(Locale.ROOT).trim();
        int semicolon = lower.indexOf(';');
        if (semicolon >= 0) {
            lower = lower.substring(0, semicolon).trim();
        }
        if (lower.isBlank()) return "application/octet-stream";
        return lower;
    }

    public static boolean isAllowedMimeType(String mimeType) {
        if (mimeType == null) return false;
        String mt = normalizeContentType(mimeType);
        return mt.startsWith("image/") ||
               mt.startsWith("video/") ||
               mt.startsWith("audio/") ||
               mt.equals("application/pdf") ||
               mt.equals("application/json") ||
               mt.equals("application/msword") ||
               mt.startsWith("application/vnd.openxmlformats-officedocument") ||
               mt.startsWith("text/");
    }

    public static String detectMimeTypeFromFileName(String fileName) {
        if (fileName == null) return "application/octet-stream";
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".mp3")) return "audio/mpeg";
        if (lower.endsWith(".txt")) return "text/plain";
        if (lower.endsWith(".doc")) return "application/msword";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }
}


