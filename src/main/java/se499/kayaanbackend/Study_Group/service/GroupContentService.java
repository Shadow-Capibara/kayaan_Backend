package se499.kayaanbackend.Study_Group.service;

import java.util.List;

import se499.kayaanbackend.Study_Group.dto.ResourceResponse;
import se499.kayaanbackend.Study_Group.dto.UploadResourceCompleteRequest;
import se499.kayaanbackend.Study_Group.dto.UploadResourceInitRequest;
import se499.kayaanbackend.Study_Group.dto.UploadResourceInitResponse;

public interface GroupContentService {
    
    /**
     * Lists all resources in a group
     */
    List<ResourceResponse> listResources(Integer currentUserId, Integer groupId);
    
    /**
     * Initializes an upload by creating a signed URL
     */
    UploadResourceInitResponse initUpload(Integer currentUserId, Integer groupId, UploadResourceInitRequest request);
    
    /**
     * Completes an upload by saving the resource metadata
     */
    ResourceResponse completeUpload(Integer currentUserId, Integer groupId, UploadResourceCompleteRequest request);
    
    /**
     * Deletes a resource
     */
    void deleteResource(Integer currentUserId, Integer groupId, Long resourceId);
}
