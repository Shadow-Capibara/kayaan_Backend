package se499.kayaanbackend.Study_Group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import se499.kayaanbackend.Study_Group.service.ResourceValidationUtil;

public class ResourceValidationUtilTest {

    @Test
    void normalizeContentType_stripsCharsetAndLowercases() {
        assertEquals("application/json", ResourceValidationUtil.normalizeContentType("Application/Json; charset=UTF-8"));
        assertEquals("text/plain", ResourceValidationUtil.normalizeContentType("text/plain; Charset=latin1"));
    }

    @Test
    void isAllowedMimeType_allowsJsonAndText() {
        assertTrue(ResourceValidationUtil.isAllowedMimeType("application/json"));
        assertTrue(ResourceValidationUtil.isAllowedMimeType("text/plain"));
        assertTrue(ResourceValidationUtil.isAllowedMimeType("text/plain; charset=UTF-8"));
    }

    @Test
    void detectMimeTypeFromFileName_mapsJson() {
        assertEquals("application/json", ResourceValidationUtil.detectMimeTypeFromFileName("data.JSON"));
        assertEquals("application/pdf", ResourceValidationUtil.detectMimeTypeFromFileName("a.pdf"));
    }
}


