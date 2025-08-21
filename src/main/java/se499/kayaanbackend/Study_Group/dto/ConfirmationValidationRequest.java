package se499.kayaanbackend.Study_Group.dto;

import org.antlr.v4.runtime.misc.NotNull;

public record ConfirmationValidationRequest(
    @NotNull
    String confirmationToken
) {}
