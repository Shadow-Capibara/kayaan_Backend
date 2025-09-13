package se499.kayaanbackend.Study_Group.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import se499.kayaanbackend.Study_Group.dto.CreateCommentRequest;
import se499.kayaanbackend.Study_Group.dto.CreatePostRequest;
import se499.kayaanbackend.Study_Group.dto.GroupPostDto;
import se499.kayaanbackend.Study_Group.dto.PostAttachmentDto;
import se499.kayaanbackend.Study_Group.dto.PostCommentDto;
import se499.kayaanbackend.Study_Group.dto.UpdateCommentRequest;
import se499.kayaanbackend.Study_Group.dto.UpdatePostRequest;
import se499.kayaanbackend.Study_Group.entity.CommentLike;
import se499.kayaanbackend.Study_Group.entity.GroupPost;
import se499.kayaanbackend.Study_Group.entity.PostAttachment;
import se499.kayaanbackend.Study_Group.entity.PostComment;
import se499.kayaanbackend.Study_Group.entity.PostLike;
import se499.kayaanbackend.Study_Group.repository.CommentLikeRepository;
import se499.kayaanbackend.Study_Group.repository.GroupPostRepository;
import se499.kayaanbackend.Study_Group.repository.PostAttachmentRepository;
import se499.kayaanbackend.Study_Group.repository.PostCommentRepository;
import se499.kayaanbackend.Study_Group.repository.PostLikeRepository;
import se499.kayaanbackend.Study_Group.repository.StudyGroupRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

@Service
@Transactional
public class GroupPostServiceImpl implements GroupPostService {
    
    @Autowired
    private GroupPostRepository groupPostRepository;
    
    @Autowired
    private PostAttachmentRepository postAttachmentRepository;
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    
    @Autowired
    private StudyGroupRepository studyGroupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.upload.path:uploads}")
    private String uploadPath;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    @Override
    public GroupPostDto createPost(CreatePostRequest request, Integer authorId, List<MultipartFile> attachments) {
        // Validate group exists
        if (!studyGroupRepository.existsById(request.getGroupId())) {
            throw new IllegalArgumentException("Study group not found");
        }
        
        // Validate author exists
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        
        // Create post
        GroupPost post = GroupPost.builder()
            .id(UUID.randomUUID().toString())
            .group(studyGroupRepository.getReferenceById(request.getGroupId()))
            .author(author)
            .title(request.getTitle())
            .description(request.getDescription())
            .content(request.getContent())
            .contentType(request.getContentType())
            .tags(convertTagsToJson(request.getTags()))
            .build();
        
        post = groupPostRepository.save(post);
        
        // Handle attachments if provided
        if (attachments != null && !attachments.isEmpty()) {
            List<String> uploadedFiles = uploadPostAttachments(post.getId(), attachments, authorId);
            // Update content type if files were uploaded
            if (!uploadedFiles.isEmpty() && post.getContentType() == GroupPost.ContentType.TEXT) {
                post.setContentType(GroupPost.ContentType.MIXED);
                groupPostRepository.save(post);
            }
        }
        
        return convertToDto(post);
    }
    
    @Override
    public GroupPostDto getPostById(String postId) {
        GroupPost post = groupPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return convertToSimpleDto(post);
    }
    
    @Override
    @Transactional(readOnly = true)
    public GroupPostDto getPostWithComments(String postId) {
        // Use a custom query to fetch post with comments
        Optional<GroupPost> postOpt = groupPostRepository.findById(postId);
        if (postOpt.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }
        
        GroupPost post = postOpt.get();
        
        // Manually fetch comments to avoid lazy loading issues
        List<PostComment> comments = postCommentRepository.findByPostIdWithAuthor(postId);
        post.setComments(new HashSet<>(comments));
        
        return convertToDto(post);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<GroupPostDto> getPostsByGroupId(Integer groupId, Pageable pageable) {
        try {
            Page<GroupPost> posts = groupPostRepository.findByGroupIdWithDetailsOrderByCreatedAtDesc(groupId, pageable);
            return posts.map(this::convertToSimpleDto);
        } catch (Exception e) {
            System.err.println("Error in getPostsByGroupId: " + e.getMessage());
            e.printStackTrace();
            // Fallback to simple query
            Page<GroupPost> posts = groupPostRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageable);
            return posts.map(this::convertToSimpleDto);
        }
    }
    
    @Override
    public GroupPostDto updatePost(String postId, UpdatePostRequest request, Integer userId) {
        GroupPost post = groupPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        // Check if user is the author
        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own posts");
        }
        
        // Update fields if provided
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            post.setDescription(request.getDescription());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getTags() != null) {
            post.setTags(convertTagsToJson(request.getTags()));
        }
        
        post.setIsEdited(true);
        post = groupPostRepository.save(post);
        
        return convertToDto(post);
    }
    
    @Override
    public void deletePost(String postId, Integer userId) {
        GroupPost post = groupPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        // Check if user is the author
        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own posts");
        }
        
        groupPostRepository.delete(post);
    }
    
    @Override
    public Page<GroupPostDto> searchPosts(Integer groupId, String searchTerm, Pageable pageable) {
        Page<GroupPost> posts = groupPostRepository.searchPostsByGroupId(groupId, searchTerm, pageable);
        return posts.map(this::convertToDto);
    }
    
    @Override
    public Page<GroupPostDto> getPostsByContentType(Integer groupId, GroupPost.ContentType contentType, Pageable pageable) {
        Page<GroupPost> posts = groupPostRepository.findByGroupIdAndContentTypeOrderByCreatedAtDesc(groupId, contentType, pageable);
        return posts.map(this::convertToDto);
    }
    
    @Override
    public List<GroupPostDto> getPinnedPosts(Integer groupId) {
        List<GroupPost> posts = groupPostRepository.findByGroupIdAndIsPinnedTrueOrderByCreatedAtDesc(groupId);
        return posts.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    @Override
    public void likePost(String postId, Integer userId) {
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            return; // Already liked
        }
        
        PostLike like = PostLike.builder()
            .id(UUID.randomUUID().toString())
            .post(groupPostRepository.getReferenceById(postId))
            .user(userRepository.getReferenceById(userId))
            .build();
        
        postLikeRepository.save(like);
        
        // Update likes count
        GroupPost post = groupPostRepository.findById(postId).orElseThrow();
        post.setLikesCount(post.getLikesCount() + 1);
        groupPostRepository.save(post);
    }
    
    @Override
    public void unlikePost(String postId, Integer userId) {
        Optional<PostLike> like = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (like.isPresent()) {
            postLikeRepository.delete(like.get());
            
            // Update likes count
            GroupPost post = groupPostRepository.findById(postId).orElseThrow();
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            groupPostRepository.save(post);
        }
    }
    
    @Override
    public boolean hasUserLikedPost(String postId, Integer userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
    
    @Override
    public PostCommentDto createComment(String postId, CreateCommentRequest request, Integer authorId) {
        GroupPost post = groupPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        
        PostComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = postCommentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
        }
        
        PostComment comment = PostComment.builder()
            .id(UUID.randomUUID().toString())
            .post(post)
            .author(author)
            .content(request.getContent())
            .parentComment(parentComment)
            .build();
        
        comment = postCommentRepository.save(comment);
        
        // Update comments count
        post.setCommentsCount(post.getCommentsCount() + 1);
        groupPostRepository.save(post);
        
        return convertCommentToDto(comment);
    }
    
    @Override
    public PostCommentDto updateComment(String commentId, UpdateCommentRequest request, Integer userId) {
        PostComment comment = postCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        // Check if user is the author
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }
        
        comment.setContent(request.getContent());
        comment.setIsEdited(true);
        comment = postCommentRepository.save(comment);
        
        return convertCommentToDto(comment);
    }
    
    @Override
    public void deleteComment(String commentId, Integer userId) {
        PostComment comment = postCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        // Check if user is the author
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }
        
        // Update comments count
        GroupPost post = comment.getPost();
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        groupPostRepository.save(post);
        
        postCommentRepository.delete(comment);
    }
    
    @Override
    public List<PostCommentDto> getCommentsByPostId(String postId) {
        List<PostComment> comments = postCommentRepository.findByPostIdWithAuthor(postId);
        return comments.stream().map(this::convertCommentToDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PostCommentDto> getCommentsByPostId(String postId, Pageable pageable) {
        Page<PostComment> comments = postCommentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);
        return comments.map(this::convertCommentToDto);
    }
    
    @Override
    public void likeComment(String commentId, Integer userId) {
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return; // Already liked
        }
        
        CommentLike like = CommentLike.builder()
            .id(UUID.randomUUID().toString())
            .comment(postCommentRepository.getReferenceById(commentId))
            .user(userRepository.getReferenceById(userId))
            .build();
        
        commentLikeRepository.save(like);
        
        // Update likes count
        PostComment comment = postCommentRepository.findById(commentId).orElseThrow();
        comment.setLikesCount(comment.getLikesCount() + 1);
        postCommentRepository.save(comment);
    }
    
    @Override
    public void unlikeComment(String commentId, Integer userId) {
        Optional<CommentLike> like = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (like.isPresent()) {
            commentLikeRepository.delete(like.get());
            
            // Update likes count
            PostComment comment = postCommentRepository.findById(commentId).orElseThrow();
            comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
            postCommentRepository.save(comment);
        }
    }
    
    @Override
    public boolean hasUserLikedComment(String commentId, Integer userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }
    
    @Override
    public List<String> uploadPostAttachments(String postId, List<MultipartFile> files, Integer userId) {
        GroupPost post = groupPostRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        // Check if user is the author
        if (!post.getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only upload attachments to your own posts");
        }
        
        List<String> uploadedFiles = new ArrayList<>();
        
        try {
            Path uploadDir = Paths.get(uploadPath, "posts", postId);
            Files.createDirectories(uploadDir);
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = uploadDir.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath);
                    
                    PostAttachment attachment = PostAttachment.builder()
                        .id(UUID.randomUUID().toString())
                        .post(post)
                        .fileName(file.getOriginalFilename())
                        .fileUrl("/uploads/posts/" + postId + "/" + fileName)
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .build();
                    
                    postAttachmentRepository.save(attachment);
                    uploadedFiles.add(attachment.getFileUrl());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload files", e);
        }
        
        return uploadedFiles;
    }
    
    @Override
    public void deleteAttachment(String attachmentId, Integer userId) {
        PostAttachment attachment = postAttachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));
        
        // Check if user is the post author
        if (!attachment.getPost().getAuthor().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete attachments from your own posts");
        }
        
        postAttachmentRepository.delete(attachment);
    }
    
    @Override
    public long getPostCountByGroupId(Integer groupId) {
        return groupPostRepository.countByGroupId(groupId);
    }
    
    @Override
    public long getPostCountByUserId(Integer userId) {
        return groupPostRepository.countByAuthorId(userId);
    }
    
    // Helper methods
    private GroupPostDto convertToSimpleDto(GroupPost post) {
        return GroupPostDto.builder()
            .id(post.getId())
            .groupId(post.getGroup().getId())
            .authorId(post.getAuthor().getId())
            .authorName(post.getAuthor().getUsername())
            .authorAvatar(post.getAuthor().getAvatarUrl())
            .title(post.getTitle())
            .description(post.getDescription())
            .content(post.getContent())
            .contentType(post.getContentType())
            .attachments(new ArrayList<>()) // Empty list to avoid lazy loading
            .tags(convertJsonToTags(post.getTags()))
            .createdAt(post.getCreatedAt().format(DATE_FORMATTER))
            .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().format(DATE_FORMATTER) : null)
            .likesCount(post.getLikesCount())
            .comments(new ArrayList<>()) // Empty list to avoid lazy loading
            .isEdited(post.getIsEdited())
            .isPinned(post.getIsPinned())
            .build();
    }
    
    private GroupPostDto convertToDto(GroupPost post) {
        try {
            return GroupPostDto.builder()
                .id(post.getId())
                .groupId(post.getGroup().getId())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getUsername())
                .authorAvatar(post.getAuthor().getAvatarUrl())
                .title(post.getTitle())
                .description(post.getDescription())
                .content(post.getContent())
                .contentType(post.getContentType())
                .attachments(post.getAttachments() != null ? 
                    post.getAttachments().stream()
                        .map(this::convertAttachmentToDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .tags(convertJsonToTags(post.getTags()))
                .createdAt(post.getCreatedAt().format(DATE_FORMATTER))
                .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().format(DATE_FORMATTER) : null)
                .likesCount(post.getLikesCount())
                .comments(post.getComments() != null ? 
                    post.getComments().stream()
                        .map(this::convertCommentToDto)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .isEdited(post.getIsEdited())
                .isPinned(post.getIsPinned())
                .build();
        } catch (Exception e) {
            // Log the error and return a basic DTO
            System.err.println("Error converting post to DTO: " + e.getMessage());
            e.printStackTrace();
            return GroupPostDto.builder()
                .id(post.getId())
                .groupId(post.getGroup().getId())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getUsername())
                .authorAvatar(post.getAuthor().getAvatarUrl())
                .title(post.getTitle())
                .description(post.getDescription())
                .content(post.getContent())
                .contentType(post.getContentType())
                .attachments(new ArrayList<>())
                .tags(convertJsonToTags(post.getTags()))
                .createdAt(post.getCreatedAt().format(DATE_FORMATTER))
                .updatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().format(DATE_FORMATTER) : null)
                .likesCount(post.getLikesCount())
                .comments(new ArrayList<>())
                .isEdited(post.getIsEdited())
                .isPinned(post.getIsPinned())
                .build();
        }
    }
    
    private PostAttachmentDto convertAttachmentToDto(PostAttachment attachment) {
        return PostAttachmentDto.builder()
            .id(attachment.getId())
            .fileName(attachment.getFileName())
            .fileUrl(attachment.getFileUrl())
            .fileType(attachment.getFileType())
            .fileSize(attachment.getFileSize())
            .thumbnailUrl(attachment.getThumbnailUrl())
            .createdAt(attachment.getCreatedAt().format(DATE_FORMATTER))
            .build();
    }
    
    private PostCommentDto convertCommentToDto(PostComment comment) {
        return PostCommentDto.builder()
            .id(comment.getId())
            .postId(comment.getPost().getId())
            .authorId(comment.getAuthor().getId())
            .authorName(comment.getAuthor().getUsername())
            .authorAvatar(comment.getAuthor().getAvatarUrl())
            .content(comment.getContent())
            .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
            .replies(comment.getReplies().stream()
                .map(this::convertCommentToDto)
                .collect(Collectors.toList()))
            .createdAt(comment.getCreatedAt().format(DATE_FORMATTER))
            .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().format(DATE_FORMATTER) : null)
            .likesCount(comment.getLikesCount())
            .isEdited(comment.getIsEdited())
            .build();
    }
    
    private String convertTagsToJson(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return "[\"" + String.join("\",\"", tags) + "\"]";
    }
    
    private List<String> convertJsonToTags(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        // Simple JSON array parsing - in production, use a proper JSON library
        return List.of(json.replaceAll("[\\[\\]\"]", "").split(","));
    }
}
