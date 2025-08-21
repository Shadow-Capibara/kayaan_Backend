package se499.kayaanbackend.Study_Group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Study_Group.dto.ApiResponse;
import se499.kayaanbackend.Study_Group.dto.PermissionCheckResponse;
import se499.kayaanbackend.Study_Group.security.GroupPermission;
import se499.kayaanbackend.Study_Group.security.GroupPermissionService;
import se499.kayaanbackend.Study_Group.security.PermissionIntegrationService;
import se499.kayaanbackend.Study_Group.security.ContentAuditLog;
import se499.kayaanbackend.Study_Group.repository.ContentAuditLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller สำหรับจัดการความปลอดภัยของกลุ่มเรียน
 */
@RestController
@RequestMapping("/api/study-group/security")
public class GroupSecurityController {
    
    @Autowired
    private GroupPermissionService groupPermissionService;
    
    @Autowired
    private PermissionIntegrationService permissionIntegrationService;
    
    @Autowired
    private ContentAuditLogRepository contentAuditLogRepository;
    
    /**
     * ตรวจสอบสิทธิ์ของผู้ใช้
     */
    @GetMapping("/permissions/{groupId}/{permission}")
    public ResponseEntity<ApiResponse<PermissionCheckResponse>> checkPermission(
            @PathVariable Long groupId,
            @PathVariable String permission,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            GroupPermission groupPermission = GroupPermission.valueOf(permission.toUpperCase());
            
            boolean hasPermission = groupPermissionService.hasPermission(userId, groupId, groupPermission);
            var userRole = permissionIntegrationService.getUserRole(groupId, userId);
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
     * ดึงประวัติการเข้าถึงเนื้อหาของกลุ่ม
     */
    @GetMapping("/audit/{groupId}")
    public ResponseEntity<ApiResponse<List<ContentAuditLog>>> getGroupAuditLog(
            @PathVariable Long groupId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            
            // ตรวจสอบสิทธิ์ในการดูประวัติ
            if (!groupPermissionService.hasPermission(userId, groupId, GroupPermission.VIEW_ANALYTICS)) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("Insufficient permissions to view audit log"));
            }
            
            List<ContentAuditLog> auditLogs;
            
            if (action != null) {
                auditLogs = contentAuditLogRepository.findByGroupIdAndActionOrderByTimestampDesc(groupId, action);
            } else {
                auditLogs = contentAuditLogRepository.findByGroupIdOrderByTimestampDesc(groupId);
            }
            
            return ResponseEntity.ok(ApiResponse.success("Audit log retrieved", auditLogs));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving audit log: " + e.getMessage()));
        }
    }
    
    /**
     * ดึงประวัติการเข้าถึงเนื้อหาของผู้ใช้
     */
    @GetMapping("/audit/user/{userId}")
    public ResponseEntity<ApiResponse<List<ContentAuditLog>>> getUserAuditLog(
            @PathVariable Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long currentUserId = Long.parseLong(userDetails.getUsername());
            
            // ผู้ใช้สามารถดูประวัติของตัวเองได้ หรือ admin สามารถดูได้
            if (!currentUserId.equals(userId)) {
                // TODO: Add admin check logic here
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("Insufficient permissions to view user audit log"));
            }
            
            List<ContentAuditLog> auditLogs;
            
            if (action != null) {
                auditLogs = contentAuditLogRepository.findByUserIdAndActionOrderByTimestampDesc(userId, action);
            } else {
                auditLogs = contentAuditLogRepository.findByUserIdOrderByTimestampDesc(userId);
            }
            
            return ResponseEntity.ok(ApiResponse.success("User audit log retrieved", auditLogs));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving user audit log: " + e.getMessage()));
        }
    }
    
    /**
     * ดึงสถิติการเข้าถึงเนื้อหาของกลุ่ม
     */
    @GetMapping("/analytics/{groupId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGroupAnalytics(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            
            // ตรวจสอบสิทธิ์ในการดูสถิติ
            if (!groupPermissionService.hasPermission(userId, groupId, GroupPermission.VIEW_ANALYTICS)) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("Insufficient permissions to view analytics"));
            }
            
            // ดึงสถิติต่างๆ
            Long totalViews = contentAuditLogRepository.countViewsByGroupId(groupId);
            List<ContentAuditLog> groupLogs = contentAuditLogRepository.findByGroupIdOrderByTimestampDesc(groupId);
            Long totalContent = (long) groupLogs.size();
            
            Map<String, Object> analytics = new HashMap<>();
            analytics.put("totalViews", totalViews);
            analytics.put("totalContent", totalContent);
            analytics.put("groupId", groupId);
            
            return ResponseEntity.ok(ApiResponse.success("Group analytics retrieved", analytics));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving group analytics: " + e.getMessage()));
        }
    }
}
