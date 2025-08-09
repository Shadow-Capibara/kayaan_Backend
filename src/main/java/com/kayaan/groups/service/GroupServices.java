package com.kayaan.groups.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kayaan.core.user.UserContext;
import com.kayaan.groups.entity.FileMeta;
import com.kayaan.groups.entity.Group;
import com.kayaan.groups.entity.GroupComment;
import com.kayaan.groups.entity.GroupMember;
import com.kayaan.groups.entity.GroupPost;
import com.kayaan.groups.repository.FileMetaRepository;
import com.kayaan.groups.repository.GroupCommentRepository;
import com.kayaan.groups.repository.GroupMemberRepository;
import com.kayaan.groups.repository.GroupPostRepository;
import com.kayaan.groups.repository.GroupRepository;
import com.kayaan.stream.SseHub;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServices {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupPostRepository groupPostRepository;
    private final GroupCommentRepository groupCommentRepository;
    private final FileMetaRepository fileMetaRepository;
    private final UserContext userContext;
    private final SseHub sseHub;

    @Transactional
    public Group createGroup(String name, String description) {
        Long userId = userContext.getCurrentUserId();
        Group group = Group.builder()
                .name(name)
                .description(description)
                .ownerId(userId)
                .inviteCode(UUID.randomUUID().toString().substring(0, 8))
                .build();
        Group saved = groupRepository.save(group);
        groupMemberRepository.save(GroupMember.builder()
                .id(new GroupMember.GroupMemberId(saved.getId(), userId))
                .role(GroupMember.Role.owner)
                .build());
        return saved;
    }

    public record GroupWithCount(Group group, long members) {}

    public GroupWithCount getGroup(Long id) {
        Group group = groupRepository.findById(id).orElseThrow();
        long members = groupMemberRepository.countByIdGroupId(id);
        return new GroupWithCount(group, members);
    }

    @Transactional
    public void joinGroup(Long groupId, String inviteCode) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        if (!group.getInviteCode().equals(inviteCode)) throw new IllegalArgumentException("Invalid invite code");
        Long userId = userContext.getCurrentUserId();
        if (!groupMemberRepository.existsByIdGroupIdAndIdUserId(groupId, userId)) {
            groupMemberRepository.save(GroupMember.builder()
                    .id(new GroupMember.GroupMemberId(groupId, userId))
                    .role(GroupMember.Role.member)
                    .build());
        }
    }

    @Transactional
    public void leaveGroup(Long groupId) {
        Long userId = userContext.getCurrentUserId();
        groupMemberRepository.deleteById(new GroupMember.GroupMemberId(groupId, userId));
    }

    @Transactional
    public GroupPost createPost(Long groupId, GroupPost.Type type, String content, String fileKey) {
        Long userId = userContext.getCurrentUserId();
        if (!groupMemberRepository.existsByIdGroupIdAndIdUserId(groupId, userId)) {
            throw new IllegalStateException("Not a group member");
        }
        GroupPost post = groupPostRepository.save(GroupPost.builder()
                .groupId(groupId)
                .authorId(userId)
                .type(type)
                .content(content)
                .fileKey(fileKey)
                .build());
        sseHub.broadcast("group_post", toJson(Map.of("groupId", groupId, "postId", post.getId())));
        return post;
    }

    public List<GroupPost> listPosts(Long groupId) {
        return groupPostRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    @Transactional
    public GroupComment createComment(Long postId, String content) {
        Long userId = userContext.getCurrentUserId();
        GroupComment comment = groupCommentRepository.save(GroupComment.builder()
                .postId(postId)
                .authorId(userId)
                .content(content)
                .build());
        sseHub.broadcast("group_comment", toJson(Map.of("postId", postId, "commentId", comment.getId())));
        return comment;
    }

    @Transactional
    public FileMeta attachFile(Long groupId, Long postId, String key, String name, String mime) {
        FileMeta meta = fileMetaRepository.save(FileMeta.builder()
                .groupId(groupId)
                .postId(postId)
                .key(key)
                .name(name)
                .mime(mime)
                .build());
        return meta;
    }

    private String toJson(Map<String, Object> map) {
        return map.entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\":" + (e.getValue() instanceof Number ? e.getValue() : ("\"" + e.getValue() + "\"")))
                .reduce((a, b) -> a + "," + b)
                .map(s -> "{" + s + "}")
                .orElse("{}");
    }
}


