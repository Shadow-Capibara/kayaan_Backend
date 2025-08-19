package se499.kayaanbackend.Study_Group.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    // no-op
    
    @Override
    public List<ResourceResponse> listResources(Integer currentUserId, Integer groupId) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User is not a member of this group");
        }
        
        List<GroupContent> resources = groupContentRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
        
        return resources.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private String toJson(Object data) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(data);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON payload");
        }
    }

    @Override
    public UploadResourceInitResponse initUpload(Integer currentUserId, Integer groupId, UploadResourceInitRequest request) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User is not a member of this group");
        }
        
        // Validate file size (≤ 5MB for JSON; others can be larger if needed)
        String normalizedMime = ResourceValidationUtil.normalizeContentType(request.mimeType());
        if ("application/octet-stream".equals(normalizedMime)) {
            normalizedMime = ResourceValidationUtil.detectMimeTypeFromFileName(request.fileName());
        }
        Long sizeObj = request.size();
        long size = sizeObj != null ? sizeObj : 0L;
        if ("application/json".equals(normalizedMime) && size > 5L * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "JSON file too large. Maximum allowed: 5MB");
        }
        if (!ResourceValidationUtil.isAllowedMimeType(normalizedMime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file type");
        }
        
        long start = System.nanoTime();
        GroupStorageService.UploadUrlResponse uploadResponse = groupStorageService.createSignedUploadUrl(
                groupId, request.fileName(), normalizedMime, size
        );
        long latencyMs = (System.nanoTime() - start) / 1_000_000;
        System.out.println("study-group:init-upload groupId=" + groupId + " userId=" + currentUserId +
                " storagePath=" + uploadResponse.storagePath() + " mime=" + normalizedMime + " size=" + size +
                " latencyMs=" + latencyMs);
        
        return new UploadResourceInitResponse(uploadResponse.uploadUrl(), uploadResponse.storagePath(), uploadResponse.fileUrl());
    }
    
    @Override
    public ResourceResponse completeUpload(Integer currentUserId, Integer groupId, UploadResourceCompleteRequest request) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User is not a member of this group");
        }
        
        GroupContent content = GroupContent.builder()
                .groupId(groupId)
                .uploaderId(currentUserId)
                .title(request.title())
                .type(request.type())
                .preview(request.preview() != null ? toJson(request.preview()) : null)
                .storagePath(request.storagePath())
                .fileName(extractFileName(request.storagePath()))
                .fileUrl(buildPublicUrlIfAny(request.storagePath()))
                .mimeType(ResourceValidationUtil.normalizeContentType(request.mimeType()))
                .fileSize(0L)
                .stats(request.stats() != null ? toJson(request.stats()) : null)
                .createdAt(LocalDateTime.now())
                .build();
        
        long saveStart = System.nanoTime();
        GroupContent savedContent = groupContentRepository.save(content);
        long saveLatencyMs = (System.nanoTime() - saveStart) / 1_000_000;
        System.out.println("study-group:complete-upload groupId=" + groupId + " userId=" + currentUserId +
                " storagePath=" + content.getStoragePath() + " mime=" + content.getMimeType() +
                " latencyMs=" + saveLatencyMs);
        
        return mapToResponse(savedContent);
    }
    
    @Override
    public void deleteResource(Integer currentUserId, Integer groupId, Long resourceId) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User is not a member of this group");
        }
        
        GroupContent content = groupContentRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
        
        // Check if user is the uploader or has moderator/owner role
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this group"));
        
        if (!content.getUploaderId().equals(currentUserId) && 
            member.getRole() == GroupMember.Role.member) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Only uploaders, moderators, and owners can delete resources");
        }
        
        groupContentRepository.delete(content);
    }
    
    private ResourceResponse mapToResponse(GroupContent content) {
        return new ResourceResponse(
                content.getId(),
                content.getTitle(),
                content.getType(),
                content.getPreview(),
                content.getFileUrl(),
                content.getStoragePath(),
                content.getMimeType(),
                content.getStats(),
                content.getUploaderId(),
                content.getCreatedAt()
        );
    }
    
    
    private String buildPublicUrlIfAny(String storagePath) {
        if (storagePath == null) return null;
        return groupStorageService.getPublicFileUrl(storagePath);
    }

    private String extractFileName(String storagePath) {
        if (storagePath == null) return null;
        int i = storagePath.lastIndexOf('/');
        return i >= 0 ? storagePath.substring(i + 1) : storagePath;
    }

    @Override
    public String getPreviewUrl(Integer currentUserId, Integer groupId, Long resourceId) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User is not a member of this group");
        }
        GroupContent content = groupContentRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
        if (content.getStoragePath() == null) {
            return content.getFileUrl();
        }
        long t0 = System.nanoTime();
        String url = groupStorageService.createSignedGetUrl(content.getStoragePath(), 300);
        long t1 = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("study-group:preview-url groupId=" + groupId + " userId=" + currentUserId +
                " storagePath=" + content.getStoragePath() + " latencyMs=" + t1);
        return url;
    }
}
