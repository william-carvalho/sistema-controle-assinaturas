package com.acme.sistema_controle_assinaturas.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SubscriptionMetricsResponse(
        @JsonProperty("total_active") long totalActive,
        @JsonProperty("total_cancelled") long totalCancelled,
        List<PlanMetric> plans
) {
}
