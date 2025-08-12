package se499.kayaanbackend.Study_Group.dto;

import se499.kayaanbackend.Study_Group.GroupMember.Role;

public record UpdateMemberRoleRequest(
    Integer userId,
    Role role
) {}
