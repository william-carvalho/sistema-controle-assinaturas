package com.acme.sistema_controle_assinaturas.infrastructure.api.requests;

import com.acme.sistema_controle_assinaturas.application.dto.PaymentWebhookCommand;
import com.acme.sistema_controle_assinaturas.domain.model.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentWebhookRequest(
        @JsonProperty("subscription_id") @NotNull UUID subscriptionId,
        @JsonProperty("event") @NotBlank String event,
        @JsonProperty("amount") @NotNull BigDecimal amount,
        @JsonProperty("date") @NotNull LocalDate date
) {
    public PaymentWebhookCommand toCommand() {
        return new PaymentWebhookCommand(subscriptionId, EventType.fromWebhook(event), amount, date);
    }
}
