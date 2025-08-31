package se499.kayaanbackend.AI_Generate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class FileProcessingServiceTest {

    private final FileProcessingService fileProcessingService = new FileProcessingService();

    @Test
    public void testProcessTextFile() throws Exception {
        String content = "ISO Standards Overview\n\nISO 9001 - Quality Management Systems\nISO 14001 - Environmental Management";
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            content.getBytes()
        );

        String result = fileProcessingService.processFile(file);
        
        assertNotNull(result);
        assertTrue(result.contains("ISO Standards"));
        assertTrue(result.contains("Quality Management"));
        System.out.println("✅ Text file processing test passed");
        System.out.println("📄 Extracted content: " + result.substring(0, Math.min(100, result.length())) + "...");
    }

    @Test
    public void testFileTypeValidation() {
        assertTrue(fileProcessingService.isSupportedFileType("text/plain"));
        assertTrue(fileProcessingService.isSupportedFileType("application/pdf"));
        assertTrue(fileProcessingService.isSupportedFileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        assertTrue(fileProcessingService.isSupportedFileType("image/jpeg"));
        assertFalse(fileProcessingService.isSupportedFileType("application/unknown"));
        System.out.println("✅ File type validation test passed");
    }

    @Test
    public void testMaxFileSize() {
        long maxSize = fileProcessingService.getMaxFileSize();
        assertEquals(10 * 1024 * 1024, maxSize); // 10MB
        System.out.println("✅ Max file size validation test passed: " + (maxSize / 1024 / 1024) + "MB");
    }

    @Test
    public void testGetFileType() {
        assertEquals("txt", fileProcessingService.getFileType("text/plain"));
        assertEquals("pdf", fileProcessingService.getFileType("application/pdf"));
        assertEquals("docx", fileProcessingService.getFileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        assertEquals("image", fileProcessingService.getFileType("image/jpeg"));
        assertEquals("unknown", fileProcessingService.getFileType("application/unknown"));
        System.out.println("✅ File type mapping test passed");
    }
}
