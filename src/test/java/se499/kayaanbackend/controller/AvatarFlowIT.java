package se499.kayaanbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import se499.kayaanbackend.DTO.AvatarDTO;
import se499.kayaanbackend.service.AvatarService;
import se499.kayaanbackend.shared.storage.StorageService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class AvatarFlowIT {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private StorageService storageService;
    
    @MockBean
    private AvatarService avatarService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAvatarUploadUrlFlow() throws Exception {
        // Given
        String requestBody = """
            {
                "fileName": "test.jpg",
                "contentType": "image/jpeg"
            }
            """;
        
        StorageService.SignedUrl mockSignedUrl = new StorageService.SignedUrl(
            "https://test.supabase.co/storage/v1/object/sign/avatars/users/1/1234567890_test.jpg?token=abc123",
            "users/1/1234567890_test.jpg",
            600
        );
        
        when(storageService.createSignedUploadUrl(anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(mockSignedUrl);
        
        // When & Then
        mockMvc.perform(post("/api/users/1/avatar-upload-url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signedUrl").value(mockSignedUrl.url()))
                .andExpect(jsonPath("$.path").value(mockSignedUrl.path()))
                .andExpect(jsonPath("$.expiresIn").value(mockSignedUrl.expiresInSeconds()));
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAvatarUrlUpdateFlow() throws Exception {
        // Given
        String requestBody = """
            {
                "path": "users/1/1234567890_test.jpg"
            }
            """;
        
        String expectedPublicUrl = "https://test.supabase.co/storage/v1/object/public/avatars/users/1/1234567890_test.jpg";
        when(storageService.getPublicUrl(anyString(), anyString())).thenReturn(expectedPublicUrl);
        
        AvatarDTO mockAvatarDTO = new AvatarDTO();
        mockAvatarDTO.setAvatarUrl(expectedPublicUrl);
        when(avatarService.savePresetAvatar(anyLong(), anyString(), anyInt())).thenReturn(mockAvatarDTO);
        
        // When & Then
        mockMvc.perform(put("/api/users/1/avatar-url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
        
        // Verify service calls
        verify(storageService).getPublicUrl("avatars", "users/1/1234567890_test.jpg");
        verify(avatarService).savePresetAvatar(1L, expectedPublicUrl, 0);
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAvatarUploadUrlFlow_Unauthorized() throws Exception {
        // Given
        String requestBody = """
            {
                "fileName": "test.jpg",
                "contentType": "image/jpeg"
            }
            """;
        
        // When & Then - User 1 trying to access user 2's avatar
        mockMvc.perform(post("/api/users/2/avatar-upload-url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }
}
