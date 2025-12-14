package com.acme.sistema_controle_assinaturas.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    protected Plan() {
        // for JPA
    }

    private Plan(UUID id, String name, BigDecimal price, BillingCycle billingCycle) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.billingCycle = billingCycle;
    }

    public static Plan of(String name, BigDecimal price, BillingCycle billingCycle) {
        Objects.requireNonNull(name, "name is required");
        Objects.requireNonNull(price, "price is required");
        Objects.requireNonNull(billingCycle, "billingCycle is required");
        return new Plan(null, name, price, billingCycle);
    }

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }
}
