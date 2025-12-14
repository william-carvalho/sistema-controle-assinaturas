package com.acme.sistema_controle_assinaturas.application;

import com.acme.sistema_controle_assinaturas.application.dto.CreateSubscriptionCommand;
import com.acme.sistema_controle_assinaturas.domain.model.BillingCycle;
import com.acme.sistema_controle_assinaturas.domain.model.EventType;
import com.acme.sistema_controle_assinaturas.domain.model.Plan;
import com.acme.sistema_controle_assinaturas.domain.model.Subscription;
import com.acme.sistema_controle_assinaturas.domain.model.SubscriptionStatus;
import com.acme.sistema_controle_assinaturas.domain.repository.EventRecordRepository;
import com.acme.sistema_controle_assinaturas.domain.repository.PlanRepository;
import com.acme.sistema_controle_assinaturas.domain.repository.SubscriptionRepository;
import com.acme.sistema_controle_assinaturas.infrastructure.messaging.InMemorySubscriptionEventPublisher;
import com.acme.sistema_controle_assinaturas.support.TestClockConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestClockConfig.class)
class SubscriptionApplicationServiceTest {

    @Autowired
    private SubscriptionApplicationService subscriptionApplicationService;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EventRecordRepository eventRecordRepository;
    @Autowired
    private InMemorySubscriptionEventPublisher inMemorySubscriptionEventPublisher;

    @BeforeEach
    void setup() {
        subscriptionRepository.deleteAll();
        planRepository.deleteAll();
        eventRecordRepository.deleteAll();
        inMemorySubscriptionEventPublisher.clear();
    }

    @Test
    void shouldCreateSubscriptionAndPublishEvent() {
        Plan plan = planRepository.save(Plan.of("Test Plan", BigDecimal.valueOf(29.99), BillingCycle.MONTHLY));

        subscriptionApplicationService.createSubscription(new CreateSubscriptionCommand(plan.getId(), "user@example.com"));

        Subscription saved = subscriptionRepository.findAll().getFirst();
        assertThat(saved.getStatus()).isEqualTo(SubscriptionStatus.PENDING);
        assertThat(saved.getNextBillingDate()).isEqualTo(LocalDate.of(2025, 2, 1));

        assertThat(eventRecordRepository.count()).isEqualTo(1);
        assertThat(inMemorySubscriptionEventPublisher.publishedEvents())
                .singleElement()
                .extracting("type")
                .isEqualTo(EventType.SUBSCRIPTION_CREATED);
    }
}
