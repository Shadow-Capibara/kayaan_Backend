package se499.kayaanbackend.Study_Group.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.dto.ResourceResponse;
import se499.kayaanbackend.Study_Group.dto.UploadResourceCompleteRequest;
import se499.kayaanbackend.Study_Group.dto.UploadResourceInitRequest;
import se499.kayaanbackend.Study_Group.dto.UploadResourceInitResponse;
import se499.kayaanbackend.Study_Group.service.GroupContentService;
import se499.kayaanbackend.security.user.User;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupContentController {
    
    private final GroupContentService groupContentService;
    
    @GetMapping("/{groupId}/resources")
    public ResponseEntity<List<ResourceResponse>> listResources(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId) {
        List<ResourceResponse> resources = groupContentService.listResources(currentUser.getId(), groupId);
        return ResponseEntity.ok(resources);
    }
    
    @PostMapping("/{groupId}/resources/upload-url")
    public ResponseEntity<UploadResourceInitResponse> initUpload(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @RequestBody UploadResourceInitRequest request) {
        UploadResourceInitResponse response = groupContentService.initUpload(currentUser.getId(), groupId, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{groupId}/resources")
    public ResponseEntity<ResourceResponse> completeUpload(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @RequestBody UploadResourceCompleteRequest request) {
        ResourceResponse resource = groupContentService.completeUpload(currentUser.getId(), groupId, request);
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping("/{groupId}/contents/{id}/preview-url")
    public ResponseEntity<String> getPreviewUrl(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Long id) {
        String url = groupContentService.getPreviewUrl(currentUser.getId(), groupId, id);
        return ResponseEntity.ok(url);
    }
    
    @DeleteMapping("/{groupId}/resources/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Long resourceId) {
        groupContentService.deleteResource(currentUser.getId(), groupId, resourceId);
        return ResponseEntity.ok().build();
    }
}
