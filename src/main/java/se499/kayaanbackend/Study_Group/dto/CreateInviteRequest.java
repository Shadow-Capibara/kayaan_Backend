package se499.kayaanbackend.Study_Group.dto;

import org.antlr.v4.runtime.misc.NotNull;
import java.time.LocalDateTime;

public record CreateInviteRequest(
    @NotNull
    Long groupId,
    
    Integer maxUses,
    
    LocalDateTime expiresAt,
    
    String createdByIp
) {}
