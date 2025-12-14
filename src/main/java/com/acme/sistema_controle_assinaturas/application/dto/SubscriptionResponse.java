package com.acme.sistema_controle_assinaturas.application.dto;

import com.acme.sistema_controle_assinaturas.domain.model.Subscription;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionResponse(
        @JsonProperty("subscription_id") UUID subscriptionId,
        String status,
        @JsonProperty("next_billing_date") LocalDate nextBillingDate,
        @JsonProperty("plan_id") UUID planId,
        @JsonProperty("customer_email") String customerEmail
) {

    public static SubscriptionResponse from(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getStatus().name().toLowerCase(),
                subscription.getNextBillingDate(),
                subscription.getPlan().getId(),
                subscription.getCustomerEmail()
        );
    }
}
