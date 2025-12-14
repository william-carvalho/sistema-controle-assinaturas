package com.acme.sistema_controle_assinaturas.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSubscriptionCommand(
        @NotNull UUID planId,
        @NotBlank @Email String customerEmail
) {
}
