package com.acme.sistema_controle_assinaturas.domain.model.events;

import com.acme.sistema_controle_assinaturas.domain.model.EventType;
import com.acme.sistema_controle_assinaturas.domain.model.Subscription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionEventMessage(
        UUID eventId,
        EventType type,
        UUID subscriptionId,
        UUID planId,
        String customerEmail,
        BigDecimal amount,
        LocalDate eventDate
) {
    public static SubscriptionEventMessage subscriptionCreated(Subscription subscription, BigDecimal amount, LocalDate eventDate) {
        return new SubscriptionEventMessage(
                UUID.randomUUID(),
                EventType.SUBSCRIPTION_CREATED,
                subscription.getId(),
                subscription.getPlan().getId(),
                subscription.getCustomerEmail(),
                amount,
                eventDate
        );
    }

    public static SubscriptionEventMessage paymentSuccess(UUID subscriptionId, UUID planId, String customerEmail, BigDecimal amount, LocalDate eventDate) {
        return new SubscriptionEventMessage(
                UUID.randomUUID(),
                EventType.PAYMENT_SUCCESS,
                subscriptionId,
                planId,
                customerEmail,
                amount,
                eventDate
        );
    }

    public static SubscriptionEventMessage paymentFailed(UUID subscriptionId, UUID planId, String customerEmail, BigDecimal amount, LocalDate eventDate) {
        return new SubscriptionEventMessage(
                UUID.randomUUID(),
                EventType.PAYMENT_FAILED,
                subscriptionId,
                planId,
                customerEmail,
                amount,
                eventDate
        );
    }
}
