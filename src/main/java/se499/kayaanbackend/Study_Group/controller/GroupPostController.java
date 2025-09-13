package se499.kayaanbackend.Study_Group.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import se499.kayaanbackend.Study_Group.dto.CreateCommentRequest;
import se499.kayaanbackend.Study_Group.dto.CreatePostRequest;
import se499.kayaanbackend.Study_Group.dto.GroupPostDto;
import se499.kayaanbackend.Study_Group.dto.PostCommentDto;
import se499.kayaanbackend.Study_Group.dto.UpdateCommentRequest;
import se499.kayaanbackend.Study_Group.dto.UpdatePostRequest;
import se499.kayaanbackend.Study_Group.entity.GroupPost;
import se499.kayaanbackend.Study_Group.entity.GroupPost.ContentType;
import se499.kayaanbackend.Study_Group.repository.GroupPostRepository;
import se499.kayaanbackend.Study_Group.service.GroupPostService;
import se499.kayaanbackend.security.user.User;

@RestController
@RequestMapping("/api/groups")
public class GroupPostController {
    
    @Autowired
    private GroupPostService groupPostService;
    
    @Autowired
    private GroupPostRepository groupPostRepository;
    
    // Post Management Endpoints
    
    @PostMapping("/{groupId}/posts")
    public ResponseEntity<GroupPostDto> createPost(
            @PathVariable Integer groupId,
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal User user) {
        
        request.setGroupId(groupId);
        
        GroupPostDto post = groupPostService.createPost(request, user.getId(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
    @PostMapping("/{groupId}/posts/with-files")
    public ResponseEntity<GroupPostDto> createPostWithFiles(
            @PathVariable Integer groupId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String content,
            @RequestParam ContentType contentType,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) List<MultipartFile> attachments,
            @AuthenticationPrincipal User user) {
        
        CreatePostRequest request = CreatePostRequest.builder()
            .groupId(groupId)
            .title(title)
            .description(description)
            .content(content)
            .contentType(contentType)
            .tags(tags)
            .build();
        
        GroupPostDto post = groupPostService.createPost(request, user.getId(), attachments);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
    @GetMapping("/{groupId}/posts")
    public ResponseEntity<Page<GroupPostDto>> getPosts(
            @PathVariable Integer groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<GroupPostDto> posts = groupPostService.getPostsByGroupId(groupId, pageable);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            System.err.println("Error in getPosts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Page.empty());
        }
    }
    
    @GetMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<GroupPostDto> getPost(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @RequestParam(defaultValue = "false") boolean includeComments) {
        
        GroupPostDto post = includeComments ? 
            groupPostService.getPostWithComments(postId) : 
            groupPostService.getPostById(postId);
        return ResponseEntity.ok(post);
    }
    
    @GetMapping("/{groupId}/posts/{postId}/comments")
    public ResponseEntity<List<PostCommentDto>> getPostComments(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PostCommentDto> comments = groupPostService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(comments.getContent());
    }
    
    @PutMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<GroupPostDto> updatePost(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal User user) {
        
        GroupPostDto post = groupPostService.updatePost(postId, request, user.getId());
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{groupId}/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @AuthenticationPrincipal User user) {
        
        groupPostService.deletePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }
    
    // Post Search and Filtering
    
    @GetMapping("/{groupId}/posts/search")
    public ResponseEntity<Page<GroupPostDto>> searchPosts(
            @PathVariable Integer groupId,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GroupPostDto> posts = groupPostService.searchPosts(groupId, q, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{groupId}/posts/filter")
    public ResponseEntity<Page<GroupPostDto>> getPostsByContentType(
            @PathVariable Integer groupId,
            @RequestParam ContentType contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GroupPostDto> posts = groupPostService.getPostsByContentType(groupId, contentType, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{groupId}/posts/pinned")
    public ResponseEntity<List<GroupPostDto>> getPinnedPosts(@PathVariable Integer groupId) {
        List<GroupPostDto> posts = groupPostService.getPinnedPosts(groupId);
        return ResponseEntity.ok(posts);
    }
    
    // Post Interactions
    
    @PostMapping("/{groupId}/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @AuthenticationPrincipal User user) {
        groupPostService.likePost(postId, user.getId());
        
        // Get updated likes count
        GroupPost post = groupPostRepository.findById(postId).orElseThrow();
        
        Map<String, Object> response = new HashMap<>();
        response.put("likesCount", post.getLikesCount());
        response.put("message", "Post liked successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{groupId}/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> unlikePost(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @AuthenticationPrincipal User user) {
        groupPostService.unlikePost(postId, user.getId());
        
        // Get updated likes count
        GroupPost post = groupPostRepository.findById(postId).orElseThrow();
        
        Map<String, Object> response = new HashMap<>();
        response.put("likesCount", post.getLikesCount());
        response.put("message", "Post unliked successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{groupId}/posts/{postId}/liked")
    public ResponseEntity<Boolean> hasUserLikedPost(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @AuthenticationPrincipal User user) {
        boolean liked = groupPostService.hasUserLikedPost(postId, user.getId());
        return ResponseEntity.ok(liked);
    }
    
    // Comment Management
    
    @PostMapping("/{groupId}/posts/{postId}/comments")
    public ResponseEntity<PostCommentDto> createComment(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal User user) {
        PostCommentDto comment = groupPostService.createComment(postId, request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
    
    
    @PutMapping("/{groupId}/posts/{postId}/comments/{commentId}")
    public ResponseEntity<PostCommentDto> updateComment(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal User user) {
        PostCommentDto comment = groupPostService.updateComment(commentId, request, user.getId());
        return ResponseEntity.ok(comment);
    }
    
    @DeleteMapping("/{groupId}/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @PathVariable String commentId,
            @AuthenticationPrincipal User user) {
        groupPostService.deleteComment(commentId, user.getId());
        return ResponseEntity.noContent().build();
    }
    
    // Comment Interactions
    
    @PostMapping("/{groupId}/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @PathVariable String commentId,
            @AuthenticationPrincipal User user) {
        groupPostService.likeComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{groupId}/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @PathVariable String commentId,
            @AuthenticationPrincipal User user) {
        groupPostService.unlikeComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{groupId}/posts/{postId}/comments/{commentId}/liked")
    public ResponseEntity<Boolean> hasUserLikedComment(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @PathVariable String commentId,
            @AuthenticationPrincipal User user) {
        boolean liked = groupPostService.hasUserLikedComment(commentId, user.getId());
        return ResponseEntity.ok(liked);
    }
    
    // File Attachment Management
    
    @PostMapping("/{groupId}/posts/{postId}/attachments")
    public ResponseEntity<List<String>> uploadAttachments(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @RequestParam List<MultipartFile> files,
            @AuthenticationPrincipal User user) {
        List<String> uploadedFiles = groupPostService.uploadPostAttachments(postId, files, user.getId());
        return ResponseEntity.ok(uploadedFiles);
    }
    
    @DeleteMapping("/{groupId}/posts/{postId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Integer groupId,
            @PathVariable String postId,
            @PathVariable String attachmentId,
            @AuthenticationPrincipal User user) {
        groupPostService.deleteAttachment(attachmentId, user.getId());
        return ResponseEntity.noContent().build();
    }
    
    // Statistics
    
    @GetMapping("/{groupId}/posts/count")
    public ResponseEntity<Long> getPostCount(@PathVariable Integer groupId) {
        long count = groupPostService.getPostCountByGroupId(groupId);
        return ResponseEntity.ok(count);
    }
}
