package se499.kayaanbackend.infra.supabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class SupabaseStorageAdapterIT {
    
    @Test
    void testGetPublicUrl() {
        // Given
        SupabaseStorageAdapter storageAdapter = new SupabaseStorageAdapter("https://test.supabase.co", "test-service-key");
        
        // When
        String result = storageAdapter.getPublicUrl("avatars", "users/1/test.jpg");
        
        // Then
        assertEquals("https://test.supabase.co/storage/v1/object/public/avatars/users/1/test.jpg", result);
    }
    
    @Test
    void testJsonParsing() throws Exception {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = """
            {
                "signedURL": "https://test.supabase.co/storage/v1/object/sign/avatars/users/1/1234567890_test.jpg?token=abc123",
                "path": "users/1/1234567890_test.jpg"
            }
            """;
        
        // When
        SupabaseStorageAdapter.SignedUrlResponse response = objectMapper.readValue(jsonResponse, SupabaseStorageAdapter.SignedUrlResponse.class);
        
        // Then
        assertNotNull(response);
        assertEquals("https://test.supabase.co/storage/v1/object/sign/avatars/users/1/1234567890_test.jpg?token=abc123", response.signedURL);
        assertEquals("users/1/1234567890_test.jpg", response.path);
    }
}
