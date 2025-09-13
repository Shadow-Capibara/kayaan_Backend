package se499.kayaanbackend.Study_Group.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.Study_Group.entity.GroupPost.ContentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    
    @NotNull(message = "Group ID is required")
    private Integer groupId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Content type is required")
    private ContentType contentType;
    
    private List<String> tags;
    
    // Note: File attachments will be handled separately in the controller
    // as they require multipart/form-data handling
}
