package se499.kayaanbackend.Study_Group.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.Study_Group.entity.GroupPost.ContentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPostDto {
    
    private String id;
    private Integer groupId;
    private Integer authorId;
    private String authorName;
    private String authorAvatar;
    private String title;
    private String description;
    private String content;
    private ContentType contentType;
    private List<PostAttachmentDto> attachments;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;
    private Integer likesCount;
    private List<PostCommentDto> comments;
    private Boolean isEdited;
    private Boolean isPinned;
}
