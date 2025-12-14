package com.acme.sistema_controle_assinaturas.domain.service;

import com.acme.sistema_controle_assinaturas.domain.model.BillingCycle;

import java.time.LocalDate;

public final class BillingDateCalculator {

    private BillingDateCalculator() {
    }

    public static LocalDate nextBillingDate(BillingCycle cycle, LocalDate reference) {
        return switch (cycle) {
            case MONTHLY -> reference.plusMonths(1);
            case YEARLY -> reference.plusYears(1);
        };
    }
}
