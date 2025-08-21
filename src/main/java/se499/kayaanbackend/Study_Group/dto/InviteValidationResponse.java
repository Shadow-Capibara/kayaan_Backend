package se499.kayaanbackend.Study_Group.dto;

import se499.kayaanbackend.Study_Group.GroupInvite;

public record InviteValidationResponse(
    boolean isValid,
    String message,
    GroupInvite invite,
    Long groupId,
    String groupName
) {}
