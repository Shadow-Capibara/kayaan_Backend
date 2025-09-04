package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

import se499.kayaanbackend.Study_Group.GroupMember.Role;

public record MemberResponse(
    Integer userId,
    String username,
    String email,
    String firstName,
    String lastName,
    String avatarUrl,
    Role role,
    LocalDateTime joinedAt
) {}
