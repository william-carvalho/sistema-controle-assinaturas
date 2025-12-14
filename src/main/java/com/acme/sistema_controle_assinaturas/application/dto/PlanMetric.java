package com.acme.sistema_controle_assinaturas.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record PlanMetric(
        @JsonProperty("plan_id") UUID planId,
        String name,
        @JsonProperty("active_subscriptions") long activeSubscriptions
) {
}
