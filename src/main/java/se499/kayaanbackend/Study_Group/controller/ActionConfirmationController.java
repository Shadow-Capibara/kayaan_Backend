package se499.kayaanbackend.Study_Group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Study_Group.dto.ApiResponse;
import se499.kayaanbackend.Study_Group.dto.ConfirmationRequest;
import se499.kayaanbackend.Study_Group.dto.ConfirmationTokenResponse;
import se499.kayaanbackend.Study_Group.dto.ConfirmationValidationRequest;
import se499.kayaanbackend.Study_Group.dto.ConfirmationValidationResponse;
import se499.kayaanbackend.Study_Group.dto.ActionExecutionRequest;
import se499.kayaanbackend.Study_Group.dto.ActionExecutionResponse;
import se499.kayaanbackend.Study_Group.security.ActionConfirmationService;
import se499.kayaanbackend.Study_Group.security.ActionConfirmationService.ConfirmationToken;
import se499.kayaanbackend.Study_Group.security.ActionConfirmationService.ConfirmationAction;
import se499.kayaanbackend.Study_Group.security.GroupPermissionService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller สำหรับจัดการการยืนยันการกระทำที่สำคัญ
 */
@RestController
@RequestMapping("/api/study-group/confirmations")
public class ActionConfirmationController {
    
    @Autowired
    private ActionConfirmationService actionConfirmationService;
    
    @Autowired
    private GroupPermissionService groupPermissionService;
    
    /**
     * สร้างโทเค็นสำหรับการยืนยันการกระทำ
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<ConfirmationTokenResponse>> requestConfirmation(
            @RequestBody ConfirmationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            
            // ตรวจสอบสิทธิ์ตามประเภทการกระทำ
            if (!hasPermissionForAction(userId, request.action(), request.targetId())) {
                return ResponseEntity.status(403)
                    .body(ApiResponse.error("Insufficient permissions for this action"));
            }
            
            // แปลง action string เป็น ConfirmationAction enum
            ConfirmationAction action = parseConfirmationAction(request.action());
            if (action == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid action type: " + request.action()));
            }
            
            // สร้าง parameters map
            Map<String, Object> params = new HashMap<>();
            params.put("targetId", request.targetId());
            params.put("reason", request.reason());
            params.put("additionalData", request.additionalData());
            
            ConfirmationToken confirmationToken = actionConfirmationService.createConfirmation(
                userId, action, params
            );
            
            ConfirmationTokenResponse response = new ConfirmationTokenResponse(
                confirmationToken.getToken(),
                confirmationToken.getExpiresAt(),
                request.action(),
                request.targetId(),
                "Confirmation token created successfully"
            );
            
            return ResponseEntity.ok(ApiResponse.success("Confirmation token created", response));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error creating confirmation token: " + e.getMessage()));
        }
    }
    
    /**
     * ตรวจสอบความถูกต้องของโทเค็นการยืนยัน
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ConfirmationValidationResponse>> validateConfirmation(
            @RequestBody ConfirmationValidationRequest request) {
        
        try {
            // ตรวจสอบ token โดยใช้ action ที่เหมาะสม (ต้องรู้ action type)
            // สำหรับการตรวจสอบทั่วไป เราจะใช้ REMOVE_MEMBER เป็น default
            boolean isValid = actionConfirmationService.validateConfirmation(
                request.confirmationToken(), ConfirmationAction.REMOVE_MEMBER
            );
            
            if (isValid) {
                // สร้าง response โดยไม่ต้องดึงข้อมูลจาก token store
                ConfirmationValidationResponse response = new ConfirmationValidationResponse(
                    true,
                    "Confirmation token is valid",
                    "REMOVE_MEMBER", // Default action
                    null, // Target ID not available without storing it separately
                    "Token validated successfully"
                );
                
                return ResponseEntity.ok(ApiResponse.success("Confirmation token validated", response));
            } else {
                ConfirmationValidationResponse response = new ConfirmationValidationResponse(
                    false,
                    "Invalid or expired confirmation token",
                    null, null, null
                );
                
                return ResponseEntity.ok(ApiResponse.success("Confirmation token validation failed", response));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error validating confirmation token: " + e.getMessage()));
        }
    }
    
    /**
     * ดำเนินการหลังจากได้รับการยืนยัน
     */
    @PostMapping("/execute")
    public ResponseEntity<ApiResponse<ActionExecutionResponse>> executeAction(
            @RequestBody ActionExecutionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            
            // ตรวจสอบความถูกต้องของโทเค็น (ใช้ default action)
            boolean isValid = actionConfirmationService.validateConfirmation(
                request.confirmationToken(), ConfirmationAction.REMOVE_MEMBER
            );
            
            if (!isValid) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired confirmation token"));
            }
            
            // ดำเนินการตามประเภทการกระทำ (simplified version)
            Object result = executeActionByType("REMOVE_MEMBER", 0, request.additionalData());
            
            // ใช้โทเค็นหลังจากใช้งานแล้ว
            actionConfirmationService.consumeToken(request.confirmationToken());
            
            ActionExecutionResponse response = new ActionExecutionResponse(
                true,
                "Action executed successfully",
                "REMOVE_MEMBER",
                0L,
                result
            );
            
            return ResponseEntity.ok(ApiResponse.success("Action executed successfully", response));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error executing action: " + e.getMessage()));
        }
    }
    
    /**
     * แปลง action string เป็น ConfirmationAction enum
     */
    private ConfirmationAction parseConfirmationAction(String action) {
        try {
            return ConfirmationAction.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * ตรวจสอบสิทธิ์ตามประเภทการกระทำ
     */
    private boolean hasPermissionForAction(Long userId, String action, Long targetId) {
        // TODO: Implement proper permission checking based on action type
        // This is a simplified version - you should implement proper permission logic
        
        switch (action.toUpperCase()) {
            case "DELETE_GROUP":
                return groupPermissionService.canDeleteGroup(userId, targetId);
            case "REMOVE_MEMBER":
                return groupPermissionService.canManageMembers(userId, targetId);
            case "DELETE_CONTENT":
                return groupPermissionService.canDeleteContent(userId, targetId);
            case "CHANGE_ROLE":
                return groupPermissionService.canManageMembers(userId, targetId);
            default:
                return false;
        }
    }
    
    /**
     * ดำเนินการตามประเภทการกระทำ
     */
    private Object executeActionByType(String action, Integer targetId, String additionalData) {
        // TODO: Implement actual action execution logic
        // This is a placeholder - you should implement the actual business logic
        
        switch (action.toUpperCase()) {
            case "DELETE_GROUP":
                // return studyGroupService.deleteGroup(targetId);
                return "Group deletion initiated";
            case "REMOVE_MEMBER":
                // return groupMemberService.removeMember(targetId);
                return "Member removal initiated";
            case "DELETE_CONTENT":
                // return groupContentService.deleteContent(targetId);
                return "Content deletion initiated";
            case "CHANGE_ROLE":
                // return groupMemberService.changeRole(targetId, additionalData);
                return "Role change initiated";
            default:
                throw new IllegalArgumentException("Unknown action type: " + action);
        }
    }
}
