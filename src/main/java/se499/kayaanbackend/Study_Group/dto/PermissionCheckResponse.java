package se499.kayaanbackend.Study_Group.dto;

import se499.kayaanbackend.Study_Group.security.GroupRole;
import se499.kayaanbackend.Study_Group.security.GroupPermission;

import java.util.Set;

public record PermissionCheckResponse(
    Long userId,
    Long groupId,
    GroupRole role,
    Set<GroupPermission> permissions,
    boolean hasPermission,
    String message
) {}
