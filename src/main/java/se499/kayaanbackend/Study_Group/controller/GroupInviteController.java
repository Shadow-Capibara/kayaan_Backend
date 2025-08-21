package se499.kayaanbackend.Study_Group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Study_Group.dto.ApiResponse;
import se499.kayaanbackend.Study_Group.dto.CreateInviteRequest;
import se499.kayaanbackend.Study_Group.GroupInvite;
import se499.kayaanbackend.Study_Group.security.GroupPermission;
import se499.kayaanbackend.Study_Group.security.GroupPermissionService;
import se499.kayaanbackend.Study_Group.security.InviteCodeService;
import se499.kayaanbackend.Study_Group.repository.GroupInviteRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller สำหรับจัดการรหัสเชิญของกลุ่มเรียน
 */
@RestController
@RequestMapping("/api/study-group/invites")
public class GroupInviteController {
    
    @Autowired
    private InviteCodeService inviteCodeService;
    
    @Autowired
    private GroupPermissionService groupPermissionService;
    
    @Autowired
    private GroupInviteRepository groupInviteRepository;
    
    /**
     * สร้างรหัสเชิญใหม่
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GroupInvite>> createInvite(
            @RequestBody CreateInviteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            
            // ตรวจสอบสิทธิ์ในการเชิญสมาชิก
            if (!groupPermissionService.hasPermission(userId, request.groupId(), GroupPermission.INVITE_MEMBERS)) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("Insufficient permissions to create invites"));
            }
            
            // ตั้งค่าวันหมดอายุถ้าไม่ได้ระบุ
            LocalDateTime expiresAt = request.expiresAt();
            if (expiresAt == null) {
                expiresAt = LocalDateTime.now().plusDays(7); // หมดอายุใน 7 วัน
            }
            
            GroupInvite invite = inviteCodeService.createInviteCode(
                request.groupId().intValue(),
                userId.intValue(),
                request.createdByIp(),
                expiresAt,
                request.maxUses()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Invite code created successfully", invite));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error creating invite code: " + e.getMessage()));
        }
    }
    
    /**
     * ดึงรายการรหัสเชิญของกลุ่ม
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<GroupInvite>>> getGroupInvites(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            
            // ตรวจสอบสิทธิ์ในการดูรหัสเชิญ
            if (!groupPermissionService.hasPermission(userId, groupId, GroupPermission.INVITE_MEMBERS)) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("Insufficient permissions to view invites"));
            }
            
            List<GroupInvite> invites = groupInviteRepository.findByGroupId(groupId.intValue());
            
            return ResponseEntity.ok(ApiResponse.success("Group invites retrieved", invites));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving group invites: " + e.getMessage()));
        }
    }
    
    /**
     * เพิกถอนรหัสเชิญ
     */
    @DeleteMapping("/revoke/{inviteCode}")
    public ResponseEntity<ApiResponse<String>> revokeInvite(
            @PathVariable String inviteCode,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            
            // ตรวจสอบว่าเป็นเจ้าของรหัสเชิญหรือมีสิทธิ์ในการจัดการ
            // TODO: Add proper permission check for invite management
            
            boolean revoked = inviteCodeService.revokeInviteCode(inviteCode, userId);
            
            if (revoked) {
                return ResponseEntity.ok(ApiResponse.success("Invite code revoked successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to revoke invite code"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error revoking invite code: " + e.getMessage()));
        }
    }
    
    /**
     * ดึงรายการรหัสเชิญที่ยังใช้งานได้
     */
    @GetMapping("/group/{groupId}/active")
    public ResponseEntity<ApiResponse<List<GroupInvite>>> getActiveGroupInvites(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            
            // ตรวจสอบสิทธิ์ในการดูรหัสเชิญ
            if (!groupPermissionService.hasPermission(userId, groupId, GroupPermission.INVITE_MEMBERS)) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("Insufficient permissions to view invites"));
            }
            
            List<GroupInvite> activeInvites = groupInviteRepository.findValidByGroupId(groupId.intValue(), LocalDateTime.now());
            
            return ResponseEntity.ok(ApiResponse.success("Active group invites retrieved", activeInvites));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving active group invites: " + e.getMessage()));
        }
    }
    
    /**
     * ตรวจสอบสถานะของรหัสเชิญ
     */
    @GetMapping("/status/{inviteCode}")
    public ResponseEntity<ApiResponse<GroupInvite>> getInviteStatus(
            @PathVariable String inviteCode,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            GroupInvite invite = groupInviteRepository.findByInviteCode(inviteCode).orElse(null);
            
            if (invite == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(ApiResponse.success("Invite status retrieved", invite));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving invite status: " + e.getMessage()));
        }
    }
}
