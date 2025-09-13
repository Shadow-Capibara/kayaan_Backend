package se499.kayaanbackend.Study_Group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostAttachmentDto {
    
    private String id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String thumbnailUrl;
    private String createdAt;
}
