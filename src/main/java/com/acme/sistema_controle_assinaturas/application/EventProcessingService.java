package com.acme.sistema_controle_assinaturas.application;

import com.acme.sistema_controle_assinaturas.domain.exception.DomainNotFoundException;
import com.acme.sistema_controle_assinaturas.domain.model.EventType;
import com.acme.sistema_controle_assinaturas.domain.model.Plan;
import com.acme.sistema_controle_assinaturas.domain.model.Subscription;
import com.acme.sistema_controle_assinaturas.domain.model.events.SubscriptionEventMessage;
import com.acme.sistema_controle_assinaturas.domain.repository.PlanRepository;
import com.acme.sistema_controle_assinaturas.domain.repository.SubscriptionRepository;
import com.acme.sistema_controle_assinaturas.domain.service.BillingDateCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class EventProcessingService {

    private static final Logger log = LoggerFactory.getLogger(EventProcessingService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final EventLogService eventLogService;
    private final Clock clock;

    public EventProcessingService(SubscriptionRepository subscriptionRepository,
                                  PlanRepository planRepository,
                                  EventLogService eventLogService,
                                  Clock clock) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.eventLogService = eventLogService;
        this.clock = clock;
    }

    @Transactional
    public void process(SubscriptionEventMessage event) {
        log.info("Processing event {} for subscription {}", event.type(), event.subscriptionId());
        switch (event.type()) {
            case SUBSCRIPTION_CREATED -> handleCreated(event);
            case PAYMENT_SUCCESS -> handlePaymentSuccess(event);
            case PAYMENT_FAILED -> handlePaymentFailed(event);
        }
        eventLogService.markProcessed(event.eventId());
    }

    private void handleCreated(SubscriptionEventMessage event) {
        // Creation is synchronous in the API, so we only mark it as processed to keep the log clean.
        // If needed, this is the point to call a billing provider.
    }

    private void handlePaymentSuccess(SubscriptionEventMessage event) {
        Subscription subscription = subscriptionRepository.findById(event.subscriptionId())
                .orElseThrow(() -> new DomainNotFoundException("Assinatura não encontrada"));
        Plan plan = planRepository.findById(event.planId())
                .orElseThrow(() -> new DomainNotFoundException("Plano não encontrado"));

        LocalDate referenceDate = event.eventDate() != null ? event.eventDate() : LocalDate.now(clock);
        subscription.activate(BillingDateCalculator.nextBillingDate(plan.getBillingCycle(), referenceDate));
        subscriptionRepository.save(subscription);
    }

    private void handlePaymentFailed(SubscriptionEventMessage event) {
        Subscription subscription = subscriptionRepository.findById(event.subscriptionId())
                .orElseThrow(() -> new DomainNotFoundException("Assinatura não encontrada"));
        subscription.suspend();
        subscriptionRepository.save(subscription);
    }
}
