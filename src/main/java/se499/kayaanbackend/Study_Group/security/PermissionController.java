package se499.kayaanbackend.Study_Group.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Study_Group.dto.ApiResponse;
import se499.kayaanbackend.Study_Group.dto.PermissionCheckResponse;

import java.util.Set;

/**
 * Controller สำหรับจัดการสิทธิ์และการตรวจสอบสิทธิ์
 */
@RestController
@RequestMapping("/api/study-group/permissions")
public class PermissionController {
    
    @Autowired
    private GroupPermissionService groupPermissionService;
    
    @Autowired
    private PermissionIntegrationService permissionIntegrationService;
    
    /**
     * ตรวจสอบสิทธิ์ของผู้ใช้
     */
    @GetMapping("/check/{groupId}/{permission}")
    public ResponseEntity<ApiResponse<PermissionCheckResponse>> checkPermission(
            @PathVariable Long groupId,
            @PathVariable String permission,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            GroupPermission groupPermission = GroupPermission.valueOf(permission.toUpperCase());
            
            boolean hasPermission = groupPermissionService.hasPermission(userId, groupId, groupPermission);
            GroupRole userRole = permissionIntegrationService.getUserRole(groupId, userId);
            Set<GroupPermission> userPermissions = permissionIntegrationService.getUserPermissions(groupId, userId);
            
            PermissionCheckResponse response = new PermissionCheckResponse(
                userId, groupId, userRole, userPermissions, hasPermission, 
                hasPermission ? "User has permission" : "User does not have permission"
            );
            
            return ResponseEntity.ok(ApiResponse.success("Permission check completed", response));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid permission: " + permission));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error checking permission: " + e.getMessage()));
        }
    }
    
    /**
     * ดึงสิทธิ์ทั้งหมดของผู้ใช้
     */
    @GetMapping("/user/{groupId}")
    public ResponseEntity<ApiResponse<Set<GroupPermission>>> getUserPermissions(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            Set<GroupPermission> permissions = permissionIntegrationService.getUserPermissions(groupId, userId);
            
            return ResponseEntity.ok(ApiResponse.success("User permissions retrieved", permissions));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving user permissions: " + e.getMessage()));
        }
    }
    
    /**
     * ดึงบทบาทของผู้ใช้
     */
    @GetMapping("/role/{groupId}")
    public ResponseEntity<ApiResponse<GroupRole>> getUserRole(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            GroupRole role = permissionIntegrationService.getUserRole(groupId, userId);
            
            if (role == null) {
                return ResponseEntity.ok(ApiResponse.error("User is not a member of this group"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("User role retrieved", role));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving user role: " + e.getMessage()));
        }
    }
    
    /**
     * ตรวจสอบสิทธิ์แบบ hierarchical
     */
    @GetMapping("/hierarchical/{groupId}/{permission}")
    public ResponseEntity<ApiResponse<Boolean>> checkHierarchicalPermission(
            @PathVariable Long groupId,
            @PathVariable String permission,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            GroupPermission groupPermission = GroupPermission.valueOf(permission.toUpperCase());
            
            boolean hasPermission = permissionIntegrationService.hasHierarchicalPermission(
                groupId, userId, groupPermission);
            
            return ResponseEntity.ok(ApiResponse.success("Hierarchical permission check completed", hasPermission));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid permission: " + permission));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error checking hierarchical permission: " + e.getMessage()));
        }
    }
}
