package com.acme.sistema_controle_assinaturas.infrastructure.api.requests;

import com.acme.sistema_controle_assinaturas.application.dto.CreateSubscriptionCommand;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSubscriptionRequest(
        @JsonProperty("plan_id") @NotNull UUID planId,
        @JsonProperty("customer_email") @NotBlank @Email String customerEmail
) {
    public CreateSubscriptionCommand toCommand() {
        return new CreateSubscriptionCommand(planId, customerEmail);
    }
}
