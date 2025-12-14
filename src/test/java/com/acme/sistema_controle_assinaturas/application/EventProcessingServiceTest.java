package com.acme.sistema_controle_assinaturas.application;

import com.acme.sistema_controle_assinaturas.domain.model.BillingCycle;
import com.acme.sistema_controle_assinaturas.domain.model.Plan;
import com.acme.sistema_controle_assinaturas.domain.model.Subscription;
import com.acme.sistema_controle_assinaturas.domain.model.SubscriptionStatus;
import com.acme.sistema_controle_assinaturas.domain.model.events.SubscriptionEventMessage;
import com.acme.sistema_controle_assinaturas.domain.repository.EventRecordRepository;
import com.acme.sistema_controle_assinaturas.domain.repository.PlanRepository;
import com.acme.sistema_controle_assinaturas.domain.repository.SubscriptionRepository;
import com.acme.sistema_controle_assinaturas.support.TestClockConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestClockConfig.class)
class EventProcessingServiceTest {

    @Autowired
    private EventProcessingService eventProcessingService;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EventLogService eventLogService;
    @Autowired
    private EventRecordRepository eventRecordRepository;
    @Autowired
    private Clock clock;

    private Plan plan;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        subscriptionRepository.deleteAll();
        planRepository.deleteAll();
        eventRecordRepository.deleteAll();

        plan = planRepository.save(Plan.of("Processing Plan", BigDecimal.valueOf(15.00), BillingCycle.MONTHLY));
        subscription = subscriptionRepository.save(Subscription.create(
                plan,
                "processor@example.com",
                LocalDate.now(clock).plusMonths(1),
                clock
        ));
    }

    @Test
    void shouldActivateSubscriptionOnPaymentSuccess() {
        SubscriptionEventMessage event = SubscriptionEventMessage.paymentSuccess(
                subscription.getId(),
                plan.getId(),
                subscription.getCustomerEmail(),
                BigDecimal.valueOf(15.00),
                LocalDate.of(2025, 1, 1)
        );
        eventLogService.recordAndPublish(event);

        eventProcessingService.process(event);

        Subscription updated = subscriptionRepository.findById(subscription.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(updated.getNextBillingDate()).isEqualTo(LocalDate.of(2025, 2, 1));
        assertThat(eventRecordRepository.findById(event.eventId()))
                .map(record -> record.isProcessed())
                .contains(true);
    }

    @Test
    void shouldSuspendSubscriptionOnPaymentFailed() {
        SubscriptionEventMessage event = SubscriptionEventMessage.paymentFailed(
                subscription.getId(),
                plan.getId(),
                subscription.getCustomerEmail(),
                BigDecimal.valueOf(15.00),
                LocalDate.of(2025, 1, 1)
        );
        eventProcessingService.process(event);

        Subscription updated = subscriptionRepository.findById(subscription.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(SubscriptionStatus.SUSPENDED);
    }
}
