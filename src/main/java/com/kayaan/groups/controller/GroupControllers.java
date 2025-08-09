package com.kayaan.groups.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kayaan.groups.entity.GroupComment;
import com.kayaan.groups.entity.GroupPost;
import com.kayaan.groups.service.GroupServices;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class GroupControllers {
    private final GroupServices groupServices;

    // Create group
    @PostMapping("/groups")
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest req) {
        return ResponseEntity.status(201).body(groupServices.createGroup(req.getName(), req.getDescription()));
    }

    // Get group
    @GetMapping("/groups/{id}")
    public ResponseEntity<?> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(groupServices.getGroup(id));
    }

    // Join/Leave
    @PostMapping("/groups/{id}/join")
    public ResponseEntity<?> join(@PathVariable Long id, @RequestBody JoinGroupRequest req) {
        groupServices.joinGroup(id, req.getInviteCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/groups/{id}/leave")
    public ResponseEntity<?> leave(@PathVariable Long id) {
        groupServices.leaveGroup(id);
        return ResponseEntity.ok().build();
    }

    // Posts
    @PostMapping("/groups/{id}/posts")
    public ResponseEntity<?> createPost(@PathVariable Long id, @RequestBody CreatePostRequest req) {
        GroupPost post = groupServices.createPost(id, req.getType(), req.getContent(), req.getFileKey());
        return ResponseEntity.status(201).body(post);
    }

    @GetMapping("/groups/{id}/posts")
    public ResponseEntity<?> listPosts(@PathVariable Long id) {
        return ResponseEntity.ok(groupServices.listPosts(id));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> comment(@PathVariable Long postId, @RequestBody CreateCommentRequest req) {
        GroupComment c = groupServices.createComment(postId, req.getContent());
        return ResponseEntity.status(201).body(c);
    }

    @PostMapping("/groups/{id}/files")
    public ResponseEntity<?> file(@PathVariable Long id, @RequestBody AttachFileRequest req) {
        return ResponseEntity.status(201).body(
                groupServices.attachFile(id, req.getPostId(), req.getKey(), req.getName(), req.getMime())
        );
    }

    @Data
    public static class CreateGroupRequest {
        @NotBlank private String name;
        private String description;
    }

    @Data
    public static class JoinGroupRequest {
        @NotBlank private String inviteCode;
    }

    @Data
    public static class CreatePostRequest {
        @NotNull private GroupPost.Type type;
        private String content;
        private String fileKey;
    }

    @Data
    public static class CreateCommentRequest {
        @NotBlank private String content;
    }

    @Data
    public static class AttachFileRequest {
        @NotNull private Long postId;
        @NotBlank private String key;
        @NotBlank private String name;
        @NotBlank private String mime;
    }
}


