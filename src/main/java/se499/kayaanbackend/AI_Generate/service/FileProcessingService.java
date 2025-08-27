package se499.kayaanbackend.AI_Generate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Service for processing uploaded files (PDF, DOCX, TXT, Images)
 * Extracts text content and prepares it for AI processing
 */
@Slf4j
@Service
public class FileProcessingService {
    
    // Supported file types
    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> SUPPORTED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
        "text/plain" // TXT
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * Process uploaded file and extract text content
     * @param file Uploaded file
     * @return Extracted text content
     * @throws IOException If file processing fails
     */
    public String processFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is null or empty");
        }
        
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Unable to determine file type");
        }
        
        log.info("Processing file: {} ({} bytes, type: {})", 
                file.getOriginalFilename(), file.getSize(), contentType);
        
        try {
            if (SUPPORTED_DOCUMENT_TYPES.contains(contentType)) {
                return processDocument(file, contentType);
            } else if (SUPPORTED_IMAGE_TYPES.contains(contentType)) {
                return processImage(file);
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + contentType);
            }
        } catch (Exception e) {
            log.error("Failed to process file: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to process file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Process document files (PDF, DOCX, TXT)
     * @param file Document file
     * @param contentType File content type
     * @return Extracted text
     * @throws IOException If processing fails
     */
    private String processDocument(MultipartFile file, String contentType) throws IOException {
        String fileName = file.getOriginalFilename();
        
        if ("text/plain".equals(contentType)) {
            // Process TXT file
            return processTextFile(file);
        } else if ("application/pdf".equals(contentType)) {
            // Process PDF file
            return processPdfFile(file);
        } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            // Process DOCX file
            return processDocxFile(file);
        } else {
            throw new IllegalArgumentException("Unsupported document type: " + contentType);
        }
    }
    
    /**
     * Process text file
     * @param file Text file
     * @return File content
     * @throws IOException If reading fails
     */
    private String processTextFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] content = inputStream.readAllBytes();
            String text = new String(content, StandardCharsets.UTF_8);
            
            log.info("Successfully processed text file: {} ({} characters)", 
                    file.getOriginalFilename(), text.length());
            
            return text;
        }
    }
    
    /**
     * Process PDF file
     * @param file PDF file
     * @return Extracted text
     * @throws IOException If processing fails
     */
    private String processPdfFile(MultipartFile file) throws IOException {
        // TODO: Implement PDF text extraction using Apache PDFBox or similar library
        // For now, return placeholder text
        log.warn("PDF processing not yet implemented, returning placeholder text");
        
        return "PDF content extraction not yet implemented. " +
               "File: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)";
    }
    
    /**
     * Process DOCX file
     * @param file DOCX file
     * @return Extracted text
     * @throws IOException If processing fails
     */
    private String processDocxFile(MultipartFile file) throws IOException {
        // TODO: Implement DOCX text extraction using Apache POI or similar library
        // For now, return placeholder text
        log.warn("DOCX processing not yet implemented, returning placeholder text");
        
        return "DOCX content extraction not yet implemented. " +
               "File: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)";
    }
    
    /**
     * Process image file
     * @param file Image file
     * @return Extracted text (OCR result)
     * @throws IOException If processing fails
     */
    private String processImage(MultipartFile file) throws IOException {
        // TODO: Implement OCR using Tesseract or cloud OCR service
        // For now, return placeholder text
        log.warn("Image OCR not yet implemented, returning placeholder text");
        
        return "Image OCR not yet implemented. " +
               "File: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)";
    }
    
    /**
     * Get file type from content type
     * @param contentType File content type
     * @return File type string
     */
    public String getFileType(String contentType) {
        if (SUPPORTED_IMAGE_TYPES.contains(contentType)) {
            return "image";
        } else if ("application/pdf".equals(contentType)) {
            return "pdf";
        } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            return "docx";
        } else if ("text/plain".equals(contentType)) {
            return "txt";
        } else {
            return "unknown";
        }
    }
    
    /**
     * Check if file type is supported
     * @param contentType File content type
     * @return True if supported
     */
    public boolean isSupportedFileType(String contentType) {
        return SUPPORTED_IMAGE_TYPES.contains(contentType) || 
               SUPPORTED_DOCUMENT_TYPES.contains(contentType);
    }
    
    /**
     * Get maximum supported file size in bytes
     * @return Maximum file size
     */
    public long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
}
