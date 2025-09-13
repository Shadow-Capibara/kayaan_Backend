package se499.kayaanbackend.Study_Group.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import se499.kayaanbackend.Study_Group.dto.CreateCommentRequest;
import se499.kayaanbackend.Study_Group.dto.CreatePostRequest;
import se499.kayaanbackend.Study_Group.dto.GroupPostDto;
import se499.kayaanbackend.Study_Group.dto.PostCommentDto;
import se499.kayaanbackend.Study_Group.dto.UpdateCommentRequest;
import se499.kayaanbackend.Study_Group.dto.UpdatePostRequest;
import se499.kayaanbackend.Study_Group.entity.GroupPost.ContentType;

public interface GroupPostService {
    
    // Post management
    GroupPostDto createPost(CreatePostRequest request, Integer authorId, List<MultipartFile> attachments);
    GroupPostDto getPostById(String postId);
    GroupPostDto getPostWithComments(String postId);
    Page<GroupPostDto> getPostsByGroupId(Integer groupId, Pageable pageable);
    GroupPostDto updatePost(String postId, UpdatePostRequest request, Integer userId);
    void deletePost(String postId, Integer userId);
    
    // Post search and filtering
    Page<GroupPostDto> searchPosts(Integer groupId, String searchTerm, Pageable pageable);
    Page<GroupPostDto> getPostsByContentType(Integer groupId, ContentType contentType, Pageable pageable);
    List<GroupPostDto> getPinnedPosts(Integer groupId);
    
    // Post interactions
    void likePost(String postId, Integer userId);
    void unlikePost(String postId, Integer userId);
    boolean hasUserLikedPost(String postId, Integer userId);
    
    // Comment management
    PostCommentDto createComment(String postId, CreateCommentRequest request, Integer authorId);
    PostCommentDto updateComment(String commentId, UpdateCommentRequest request, Integer userId);
    void deleteComment(String commentId, Integer userId);
    List<PostCommentDto> getCommentsByPostId(String postId);
    Page<PostCommentDto> getCommentsByPostId(String postId, Pageable pageable);
    
    // Comment interactions
    void likeComment(String commentId, Integer userId);
    void unlikeComment(String commentId, Integer userId);
    boolean hasUserLikedComment(String commentId, Integer userId);
    
    // File attachment management
    List<String> uploadPostAttachments(String postId, List<MultipartFile> files, Integer userId);
    void deleteAttachment(String attachmentId, Integer userId);
    
    // Statistics
    long getPostCountByGroupId(Integer groupId);
    long getPostCountByUserId(Integer userId);
}
