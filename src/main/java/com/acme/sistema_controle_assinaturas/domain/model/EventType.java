package com.acme.sistema_controle_assinaturas.domain.model;

public enum EventType {
    SUBSCRIPTION_CREATED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED;

    public static EventType fromWebhook(String eventName) {
        return switch (eventName) {
            case "payment_success" -> PAYMENT_SUCCESS;
            case "payment_failed" -> PAYMENT_FAILED;
            default -> throw new IllegalArgumentException("Unsupported event: " + eventName);
        };
    }
}
