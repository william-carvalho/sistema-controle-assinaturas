package com.acme.sistema_controle_assinaturas.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(name = "next_billing_date", nullable = false)
    private LocalDate nextBillingDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Subscription() {
        // for JPA
    }

    private Subscription(UUID id,
                         Plan plan,
                         String customerEmail,
                         SubscriptionStatus status,
                         LocalDate nextBillingDate,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        this.id = id;
        this.plan = plan;
        this.customerEmail = customerEmail;
        this.status = status;
        this.nextBillingDate = nextBillingDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Subscription create(Plan plan, String customerEmail, LocalDate nextBillingDate, Clock clock) {
        Objects.requireNonNull(plan, "plan is required");
        Objects.requireNonNull(customerEmail, "customerEmail is required");
        Objects.requireNonNull(nextBillingDate, "nextBillingDate is required");
        LocalDateTime now = LocalDateTime.now(clock);
        return new Subscription(null, plan, customerEmail, SubscriptionStatus.PENDING, nextBillingDate, now, now);
    }

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now(Clock.systemUTC());
        }
        updatedAt = createdAt;
    }

    @PreUpdate
    public void touch() {
        updatedAt = LocalDateTime.now(Clock.systemUTC());
    }

    public void activate(LocalDate nextChargeDate) {
        this.status = SubscriptionStatus.ACTIVE;
        this.nextBillingDate = nextChargeDate;
    }

    public void suspend() {
        this.status = SubscriptionStatus.SUSPENDED;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public Plan getPlan() {
        return plan;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
