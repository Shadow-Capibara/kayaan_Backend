package se499.kayaanbackend.Study_Group.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.dto.InviteByEmailRequest;
import se499.kayaanbackend.Study_Group.dto.MemberResponse;
import se499.kayaanbackend.Study_Group.dto.UpdateMemberRoleRequest;
import se499.kayaanbackend.Study_Group.service.GroupMemberService;
import se499.kayaanbackend.security.user.User;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupMemberController {
    
    private final GroupMemberService groupMemberService;
    
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<MemberResponse>> getGroupMembers(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId) {
        List<MemberResponse> members = groupMemberService.getGroupMembers(currentUser.getId(), groupId);
        return ResponseEntity.ok(members);
    }
    
    @PutMapping("/{groupId}/members/{memberId}/role")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Integer memberId,
            @RequestBody UpdateMemberRoleRequest request) {
        MemberResponse member = groupMemberService.updateMemberRole(currentUser.getId(), groupId, memberId, request);
        return ResponseEntity.ok(member);
    }
    
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Integer memberId,
            @RequestParam(defaultValue = "false") boolean confirm) {
        
        if (!confirm) {
            throw new RuntimeException("Please confirm member removal by setting confirm=true");
        }
        
        groupMemberService.removeMember(currentUser.getId(), groupId, memberId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{groupId}/invite-by-email")
    public ResponseEntity<Void> inviteByEmail(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @RequestBody InviteByEmailRequest request) {
        groupMemberService.inviteByEmail(currentUser.getId(), groupId, request.email());
        return ResponseEntity.ok().build();
    }
}
