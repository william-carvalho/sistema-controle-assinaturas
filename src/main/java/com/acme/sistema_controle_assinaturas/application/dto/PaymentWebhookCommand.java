package com.acme.sistema_controle_assinaturas.application.dto;

import com.acme.sistema_controle_assinaturas.domain.model.EventType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentWebhookCommand(
        @NotNull UUID subscriptionId,
        @NotNull EventType eventType,
        @NotNull BigDecimal amount,
        @NotNull LocalDate eventDate
) {
}
