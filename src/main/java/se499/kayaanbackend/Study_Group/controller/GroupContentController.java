package se499.kayaanbackend.Study_Group.controller;

import java.util.List;

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

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.dto.ResourceResponse;
import se499.kayaanbackend.Study_Group.dto.UploadResourceCompleteRequest;
import se499.kayaanbackend.Study_Group.dto.UploadResourceInitRequest;
import se499.kayaanbackend.Study_Group.dto.UploadResourceInitResponse;
import se499.kayaanbackend.Study_Group.dto.UpdateResourceRequest;
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
            @PathVariable Integer groupId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ResourceResponse> resources = groupContentService.listResources(currentUser.getId(), groupId, search, type, page, size);
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
    
    @DeleteMapping("/{groupId}/resources/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Long resourceId,
            @RequestParam(defaultValue = "false") boolean confirm) {
        
        if (!confirm) {
            throw new RuntimeException("Please confirm resource deletion by setting confirm=true");
        }
        
        groupContentService.deleteResource(currentUser.getId(), groupId, resourceId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{groupId}/resources/{resourceId}")
    public ResponseEntity<ResourceResponse> updateResource(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Long resourceId,
            @RequestBody UpdateResourceRequest request) {
        ResourceResponse resource = groupContentService.updateResource(
                currentUser.getId(), groupId, resourceId, 
                request.title(), request.description(), request.tags());
        return ResponseEntity.ok(resource);
    }
}
