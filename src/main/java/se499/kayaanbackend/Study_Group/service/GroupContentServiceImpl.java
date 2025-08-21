package se499.kayaanbackend.Study_Group.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.GroupContent;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.dto.ResourceResponse;
import se499.kayaanbackend.Study_Group.dto.UploadResourceCompleteRequest;
import se499.kayaanbackend.Study_Group.dto.UploadResourceInitRequest;
import se499.kayaanbackend.Study_Group.dto.UploadResourceInitResponse;
import se499.kayaanbackend.Study_Group.repository.GroupContentRepository;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupContentServiceImpl implements GroupContentService {
    
    private final GroupContentRepository groupContentRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupStorageService groupStorageService;
    private final GroupNotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<ResourceResponse> listResources(Integer currentUserId, Integer groupId, String search, String type, int page, int size) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        List<GroupContent> resources;
        
        if (search != null && !search.trim().isEmpty()) {
            // Search by title, description, or tags
            resources = groupContentRepository.findByGroupIdAndSearchTerm(groupId, search.trim());
        } else if (type != null && !type.trim().isEmpty()) {
            // Filter by MIME type
            resources = groupContentRepository.findByGroupIdAndMimeTypeContaining(groupId, type.trim());
        } else {
            // Get all resources with pagination
            PageRequest pageRequest = PageRequest.of(page, size);
            resources = groupContentRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageRequest);
        }
        
        return resources.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public UploadResourceInitResponse initUpload(Integer currentUserId, Integer groupId, UploadResourceInitRequest request) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        // Validate file size (e.g., max 100MB)
        if (request.size() > 100 * 1024 * 1024) {
            throw new RuntimeException("File size too large. Maximum allowed: 100MB");
        }
        
        // Validate MIME type (basic check)
        if (!isValidMimeType(request.mimeType())) {
            throw new RuntimeException("Invalid file type");
        }
        
        GroupStorageService.UploadUrlResponse uploadResponse = groupStorageService.createSignedUploadUrl(
                groupId, request.fileName(), request.mimeType(), request.size()
        );
        
        return new UploadResourceInitResponse(uploadResponse.uploadUrl(), uploadResponse.fileUrl());
    }
    
    @Override
    public ResourceResponse completeUpload(Integer currentUserId, Integer groupId, UploadResourceCompleteRequest request) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        GroupContent content = GroupContent.builder()
                .groupId(groupId)
                .uploaderId(currentUserId)
                .title(request.title())
                .description(request.description())
                .fileName(request.fileName())
                .fileUrl(request.fileUrl())
                .mimeType(getMimeTypeFromFileName(request.fileName()))
                .fileSize(0L) // TODO: Get actual file size from storage
                .tags(serializeTags(request.tags()))
                .createdAt(LocalDateTime.now())
                .build();
        
        GroupContent savedContent = groupContentRepository.save(content);
        
        // Notify group members about new content
        notificationService.notifyContentUpdate(groupId, currentUserId, "new resource");
        
        return mapToResponse(savedContent);
    }
    
    @Override
    public void deleteResource(Integer currentUserId, Integer groupId, Long resourceId) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        GroupContent content = groupContentRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        
        // Check if user is the uploader or has moderator/owner role
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if (!content.getUploaderId().equals(currentUserId) && 
            member.getRole() == GroupMember.Role.member) {
            throw new RuntimeException("Access denied: Only uploaders, moderators, and owners can delete resources");
        }
        
        groupContentRepository.delete(content);
        
        // Notify about content deletion
        notificationService.notifyContentUpdate(groupId, currentUserId, "resource deletion");
    }
    
    /**
     * Updates a resource
     */
    public ResourceResponse updateResource(Integer currentUserId, Integer groupId, Long resourceId, 
                                 String title, String description, List<String> tags) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }

        GroupContent content = groupContentRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        // Check if user is the uploader or has moderator/owner role
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        if (!content.getUploaderId().equals(currentUserId) && 
            member.getRole() == GroupMember.Role.member) {
            throw new RuntimeException("Access denied: Only uploaders, moderators, and owners can update resources");
        }

        content.setTitle(title);
        content.setDescription(description);
        content.setTags(serializeTags(tags));
        content.setUpdatedAt(LocalDateTime.now());

        GroupContent updatedContent = groupContentRepository.save(content);
        
        // Notify about content update
        notificationService.notifyContentUpdate(groupId, currentUserId, "resource update");
        
        return mapToResponse(updatedContent);
    }
    
    private ResourceResponse mapToResponse(GroupContent content) {
        return new ResourceResponse(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getFileUrl(),
                content.getMimeType(),
                content.getFileSize(),
                deserializeTags(content.getTags()),
                content.getUploaderId(),
                content.getCreatedAt()
        );
    }
    
    private boolean isValidMimeType(String mimeType) {
        // Basic validation - allow common file types
        return mimeType != null && (
                mimeType.startsWith("image/") ||
                mimeType.startsWith("video/") ||
                mimeType.startsWith("audio/") ||
                mimeType.startsWith("application/pdf") ||
                mimeType.startsWith("application/msword") ||
                mimeType.startsWith("application/vnd.openxmlformats-officedocument") ||
                mimeType.startsWith("text/")
        );
    }
    
    private String getMimeTypeFromFileName(String fileName) {
        // Simple MIME type detection based on file extension
        if (fileName == null) return "application/octet-stream";
        
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".pdf")) return "application/pdf";
        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerFileName.endsWith(".png")) return "image/png";
        if (lowerFileName.endsWith(".gif")) return "image/gif";
        if (lowerFileName.endsWith(".mp4")) return "video/mp4";
        if (lowerFileName.endsWith(".mp3")) return "audio/mpeg";
        if (lowerFileName.endsWith(".txt")) return "text/plain";
        if (lowerFileName.endsWith(".doc")) return "application/msword";
        if (lowerFileName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        
        return "application/octet-stream";
    }
    
    private String serializeTags(List<String> tags) {
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
    
    private List<String> deserializeTags(String tagsJson) {
        try {
            return objectMapper.readValue(tagsJson, List.class);
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
