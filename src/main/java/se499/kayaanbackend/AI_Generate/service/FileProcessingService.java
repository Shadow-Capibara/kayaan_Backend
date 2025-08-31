package se499.kayaanbackend.AI_Generate.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

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
     * Process PDF file using Apache PDFBox
     * @param file PDF file
     * @return Extracted text
     * @throws IOException If processing fails
     */
    private String processPdfFile(MultipartFile file) throws IOException {
        PDDocument document = null;
        try {
            document = PDDocument.load(file.getInputStream());
            
            if (document.isEncrypted()) {
                log.warn("PDF file is encrypted, cannot extract text: {}", file.getOriginalFilename());
                return "PDF file is encrypted and cannot be processed. Please provide an unencrypted version.";
            }
            
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(document);
            
            // Clean up the extracted text
            text = text.trim();
            if (text.isEmpty()) {
                log.warn("No text content found in PDF: {}", file.getOriginalFilename());
                return "No text content found in the PDF file. It may be image-based or empty.";
            }
            
            log.info("Successfully extracted text from PDF: {} ({} characters)", 
                    file.getOriginalFilename(), text.length());
            
            // Limit text length to prevent token overuse (max 5000 characters)
            if (text.length() > 5000) {
                text = text.substring(0, 5000) + "\n[Text truncated due to length limit]";
                log.info("PDF text truncated to 5000 characters");
            }
            
            return text;
            
        } catch (Exception e) {
            log.error("Failed to extract text from PDF: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to process PDF file: " + e.getMessage(), e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception closeException) {
                    log.warn("Failed to close PDF document", closeException);
                }
            }
        }
    }
    
    /**
     * Process DOCX file using Apache POI
     * @param file DOCX file
     * @return Extracted text
     * @throws IOException If processing fails
     */
    private String processDocxFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            
            String text = extractor.getText();
            
            // Clean up the extracted text
            text = text.trim();
            if (text.isEmpty()) {
                log.warn("No text content found in DOCX: {}", file.getOriginalFilename());
                return "No text content found in the DOCX file.";
            }
            
            log.info("Successfully extracted text from DOCX: {} ({} characters)", 
                    file.getOriginalFilename(), text.length());
            
            // Limit text length to prevent token overuse (max 5000 characters)
            if (text.length() > 5000) {
                text = text.substring(0, 5000) + "\n[Text truncated due to length limit]";
                log.info("DOCX text truncated to 5000 characters");
            }
            
            return text;
            
        } catch (Exception e) {
            log.error("Failed to extract text from DOCX: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to process DOCX file: " + e.getMessage(), e);
        }
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
