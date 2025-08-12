package se499.kayaanbackend.Study_Group.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<List<MemberResponse>> getMembers(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId) {
        List<MemberResponse> members = groupMemberService.getMembers(currentUser.getId(), groupId);
        return ResponseEntity.ok(members);
    }
    
    @PatchMapping("/{groupId}/members/{userId}")
    public ResponseEntity<MemberResponse> updateRole(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @RequestBody UpdateMemberRoleRequest request) {
        MemberResponse member = groupMemberService.updateRole(currentUser.getId(), groupId, userId, request);
        return ResponseEntity.ok(member);
    }
    
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Integer userId) {
        groupMemberService.removeMember(currentUser.getId(), groupId, userId);
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
