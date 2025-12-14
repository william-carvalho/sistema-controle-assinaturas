package com.acme.sistema_controle_assinaturas.infrastructure.messaging;

import com.acme.sistema_controle_assinaturas.domain.messaging.SubscriptionEventPublisher;
import com.acme.sistema_controle_assinaturas.domain.model.events.SubscriptionEventMessage;
import com.acme.sistema_controle_assinaturas.infrastructure.config.MessagingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.mock", havingValue = "false")
public class RabbitSubscriptionEventPublisher implements SubscriptionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitSubscriptionEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties properties;

    public RabbitSubscriptionEventPublisher(RabbitTemplate rabbitTemplate, MessagingProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @Override
    public void publish(SubscriptionEventMessage event) {
        try {
            rabbitTemplate.convertAndSend(properties.exchange(), properties.routingKey(), event);
            log.debug("Published event {} to RabbitMQ", event.eventId());
        } catch (AmqpException ex) {
            log.warn("Failed to publish event {} to RabbitMQ: {}", event.eventId(), ex.getMessage());
        }
    }
}
