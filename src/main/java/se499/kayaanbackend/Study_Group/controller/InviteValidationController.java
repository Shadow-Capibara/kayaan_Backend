package se499.kayaanbackend.Study_Group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Study_Group.dto.ApiResponse;
import se499.kayaanbackend.Study_Group.dto.InviteValidationRequest;
import se499.kayaanbackend.Study_Group.dto.InviteValidationResponse;
import se499.kayaanbackend.Study_Group.security.InviteCodeService;
import se499.kayaanbackend.Study_Group.security.InviteCodeService.InviteValidationResult;
import se499.kayaanbackend.Study_Group.GroupInvite;
import se499.kayaanbackend.Study_Group.StudyGroup;
import se499.kayaanbackend.Study_Group.repository.StudyGroupRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller สำหรับตรวจสอบและจัดการรหัสเชิญ
 */
@RestController
@RequestMapping("/api/study-group/invite-validation")
public class InviteValidationController {
    
    @Autowired
    private InviteCodeService inviteCodeService;
    
    @Autowired
    private StudyGroupRepository studyGroupRepository;
    
    /**
     * ตรวจสอบความถูกต้องของรหัสเชิญ
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<InviteValidationResponse>> validateInvite(
            @RequestBody InviteValidationRequest request) {
        
        try {
            InviteValidationResult result = inviteCodeService.validateInviteCode(request.inviteCode());
            
            if (result.isValid()) {
                GroupInvite invite = result.getInvite();
                StudyGroup group = studyGroupRepository.findById(invite.getGroupId()).orElse(null);
                
                String groupName = group != null ? group.getName() : "Unknown Group";
                
                InviteValidationResponse response = new InviteValidationResponse(
                    true, result.getMessage(), invite, 
                    invite.getGroupId().longValue(), groupName
                );
                
                return ResponseEntity.ok(ApiResponse.success("Invite validation successful", response));
            } else {
                InviteValidationResponse response = new InviteValidationResponse(
                    false, result.getMessage(), null, null, null
                );
                
                return ResponseEntity.ok(ApiResponse.success("Invite validation failed", response));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error validating invite: " + e.getMessage()));
        }
    }
    
    /**
     * ตรวจสอบรหัสเชิญแบบ GET (สำหรับการตรวจสอบแบบง่าย)
     */
    @GetMapping("/validate/{inviteCode}")
    public ResponseEntity<ApiResponse<InviteValidationResponse>> validateInviteGet(
            @PathVariable String inviteCode) {
        
        try {
            InviteValidationResult result = inviteCodeService.validateInviteCode(inviteCode);
            
            if (result.isValid()) {
                GroupInvite invite = result.getInvite();
                StudyGroup group = studyGroupRepository.findById(invite.getGroupId()).orElse(null);
                
                String groupName = group != null ? group.getName() : "Unknown Group";
                
                InviteValidationResponse response = new InviteValidationResponse(
                    true, result.getMessage(), invite, 
                    invite.getGroupId().longValue(), groupName
                );
                
                return ResponseEntity.ok(ApiResponse.success("Invite validation successful", response));
            } else {
                InviteValidationResponse response = new InviteValidationResponse(
                    false, result.getMessage(), null, null, null
                );
                
                return ResponseEntity.ok(ApiResponse.success("Invite validation failed", response));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error validating invite: " + e.getMessage()));
        }
    }
    
    /**
     * ตรวจสอบสถานะของรหัสเชิญ
     */
    @GetMapping("/status/{inviteCode}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInviteStatus(@PathVariable String inviteCode) {
        
        try {
            InviteValidationResult result = inviteCodeService.validateInviteCode(inviteCode);
            
            Map<String, Object> status = new HashMap<>();
            status.put("inviteCode", inviteCode);
            status.put("isValid", result.isValid());
            status.put("message", result.getMessage());
            status.put("isExpired", result.getInvite() != null && 
                inviteCodeService.isInviteExpired(result.getInvite()));
            status.put("isUsageExceeded", result.getInvite() != null && 
                inviteCodeService.isInviteUsageExceeded(result.getInvite()));
            
            return ResponseEntity.ok(ApiResponse.success("Invite status retrieved", status));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving invite status: " + e.getMessage()));
        }
    }
    
    /**
     * ตรวจสอบรหัสเชิญหลายรหัสพร้อมกัน
     */
    @PostMapping("/validate-batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateMultipleInvites(
            @RequestBody String[] inviteCodes) {
        
        try {
            List<Map<String, Object>> validationResults = new ArrayList<>();
            
            for (String inviteCode : inviteCodes) {
                InviteValidationResult result = inviteCodeService.validateInviteCode(inviteCode);
                
                Map<String, Object> validationResult = new HashMap<>();
                validationResult.put("inviteCode", inviteCode);
                validationResult.put("isValid", result.isValid());
                validationResult.put("message", result.getMessage());
                
                validationResults.add(validationResult);
            }
            
            Map<String, Object> results = new HashMap<>();
            results.put("inviteCodes", inviteCodes);
            results.put("validationResults", validationResults);
            
            return ResponseEntity.ok(ApiResponse.success("Batch invite validation completed", results));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error validating invites: " + e.getMessage()));
        }
    }
}
