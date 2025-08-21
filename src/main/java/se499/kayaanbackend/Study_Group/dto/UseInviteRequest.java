package se499.kayaanbackend.Study_Group.dto;

import org.antlr.v4.runtime.misc.NotNull;

public record UseInviteRequest(
    @NotNull
    String inviteCode
) {}
