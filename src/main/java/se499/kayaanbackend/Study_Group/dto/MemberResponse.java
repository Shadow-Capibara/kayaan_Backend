package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

import se499.kayaanbackend.Study_Group.GroupMember.Role;

public record MemberResponse(
    Integer userId,
    Role role,
    LocalDateTime joinedAt
) {}
