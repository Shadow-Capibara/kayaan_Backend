package se499.kayaanbackend.Study_Group.dto;

import se499.kayaanbackend.Study_Group.security.GroupRole;

public record JoinGroupResponse(
    boolean success,
    String message,
    Long groupId,
    String groupName,
    GroupRole assignedRole,
    Long memberId
) {}
