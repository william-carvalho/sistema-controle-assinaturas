package com.acme.sistema_controle_assinaturas.infrastructure.config;

import com.acme.sistema_controle_assinaturas.domain.model.BillingCycle;
import com.acme.sistema_controle_assinaturas.domain.model.Plan;
import com.acme.sistema_controle_assinaturas.domain.repository.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PlanRepository planRepository;

    public DataSeeder(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) {
        if (planRepository.count() > 0) {
            return;
        }
        planRepository.save(Plan.of("Basic Plan", BigDecimal.valueOf(29.99), BillingCycle.MONTHLY));
        planRepository.save(Plan.of("Premium Plan", BigDecimal.valueOf(79.99), BillingCycle.MONTHLY));
        planRepository.save(Plan.of("Annual Plan", BigDecimal.valueOf(299.99), BillingCycle.YEARLY));
        log.info("Seeded default plans");
    }
}
