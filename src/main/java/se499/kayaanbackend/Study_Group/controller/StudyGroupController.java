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
import se499.kayaanbackend.Study_Group.dto.CreateGroupRequest;
import se499.kayaanbackend.Study_Group.dto.InviteResponse;
import se499.kayaanbackend.Study_Group.dto.JoinByTokenRequest;
import se499.kayaanbackend.Study_Group.dto.StudyGroupResponse;
import se499.kayaanbackend.Study_Group.service.StudyGroupService;
import se499.kayaanbackend.security.user.User;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudyGroupController {
    
    private final StudyGroupService studyGroupService;
    
    @PostMapping
    public ResponseEntity<StudyGroupResponse> createGroup(
            @AuthenticationPrincipal User user,
            @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(studyGroupService.createGroup(user.getId(), request));
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<StudyGroupResponse>> getMyGroups(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyGroupService.getMyGroups(user.getId()));
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<StudyGroupResponse> getGroup(
            @AuthenticationPrincipal User user,
            @PathVariable Integer groupId) {
        return ResponseEntity.ok(studyGroupService.getGroup(user.getId(), groupId));
    }
    
    @PostMapping("/join")
    public ResponseEntity<StudyGroupResponse> joinByToken(
            @AuthenticationPrincipal User user,
            @RequestBody JoinByTokenRequest request) {
        return ResponseEntity.ok(studyGroupService.joinByToken(user.getId(), request.token()));
    }
    
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @AuthenticationPrincipal User user,
            @PathVariable Integer groupId) {
        studyGroupService.leaveGroup(user.getId(), groupId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @AuthenticationPrincipal User user,
            @PathVariable Integer groupId) {
        studyGroupService.deleteGroup(user.getId(), groupId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{groupId}/invites")
    public ResponseEntity<InviteResponse> generateInvite(
            @AuthenticationPrincipal User user,
            @PathVariable Integer groupId) {
        return ResponseEntity.ok(studyGroupService.generateInvite(user.getId(), groupId));
    }
}
