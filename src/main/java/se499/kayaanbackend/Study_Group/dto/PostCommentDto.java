package se499.kayaanbackend.Study_Group.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDto {
    
    private String id;
    private String postId;
    private Integer authorId;
    private String authorName;
    private String authorAvatar;
    private String content;
    private String parentCommentId;
    private List<PostCommentDto> replies;
    private String createdAt;
    private String updatedAt;
    private Integer likesCount;
    private Boolean isEdited;
}
