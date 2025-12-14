package com.acme.sistema_controle_assinaturas.application;

import com.acme.sistema_controle_assinaturas.domain.messaging.SubscriptionEventPublisher;
import com.acme.sistema_controle_assinaturas.domain.model.EventRecord;
import com.acme.sistema_controle_assinaturas.domain.model.events.SubscriptionEventMessage;
import com.acme.sistema_controle_assinaturas.domain.repository.EventRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EventLogService {

    private final EventRecordRepository eventRecordRepository;
    private final SubscriptionEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public EventLogService(EventRecordRepository eventRecordRepository,
                           SubscriptionEventPublisher eventPublisher,
                           ObjectMapper objectMapper,
                           Clock clock) {
        this.eventRecordRepository = eventRecordRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Transactional
    public EventRecord recordAndPublish(SubscriptionEventMessage event) {
        String payload = serialize(event);
        EventRecord record = new EventRecord(
                event.eventId(),
                event.type(),
                event.subscriptionId(),
                payload,
                false,
                LocalDateTime.now(clock)
        );
        eventRecordRepository.save(record);
        eventPublisher.publish(event);
        return record;
    }

    @Transactional
    public void markProcessed(UUID eventId) {
        eventRecordRepository.findById(eventId).ifPresent(record -> record.markProcessed(LocalDateTime.now(clock)));
    }

    private String serialize(SubscriptionEventMessage event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize event " + event.eventId(), e);
        }
    }
}
