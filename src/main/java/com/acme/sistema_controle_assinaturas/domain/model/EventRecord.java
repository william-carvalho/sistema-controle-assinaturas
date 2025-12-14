package com.acme.sistema_controle_assinaturas.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
public class EventRecord {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false)
    private boolean processed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    protected EventRecord() {
        // for JPA
    }

    public EventRecord(UUID id, EventType type, UUID subscriptionId, String payload, boolean processed, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.subscriptionId = subscriptionId;
        this.payload = payload;
        this.processed = processed;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now(Clock.systemUTC());
        }
    }

    public void markProcessed(LocalDateTime processedAt) {
        this.processed = true;
        this.processedAt = processedAt;
    }

    public UUID getId() {
        return id;
    }

    public EventType getType() {
        return type;
    }

    public UUID getSubscriptionId() {
        return subscriptionId;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isProcessed() {
        return processed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
