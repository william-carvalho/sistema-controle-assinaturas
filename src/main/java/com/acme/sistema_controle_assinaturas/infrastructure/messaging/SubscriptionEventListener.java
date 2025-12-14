package com.acme.sistema_controle_assinaturas.infrastructure.messaging;

import com.acme.sistema_controle_assinaturas.application.EventProcessingService;
import com.acme.sistema_controle_assinaturas.domain.model.events.SubscriptionEventMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.mock", havingValue = "false")
public class SubscriptionEventListener {

    private final EventProcessingService eventProcessingService;

    public SubscriptionEventListener(EventProcessingService eventProcessingService) {
        this.eventProcessingService = eventProcessingService;
    }

    @RabbitListener(queues = "#{subscriptionQueue.name}")
    public void consume(SubscriptionEventMessage event) {
        eventProcessingService.process(event);
    }
}
