package com.acme.sistema_controle_assinaturas.domain.messaging;

import com.acme.sistema_controle_assinaturas.domain.model.events.SubscriptionEventMessage;

public interface SubscriptionEventPublisher {
    void publish(SubscriptionEventMessage event);
}
