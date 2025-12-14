package com.acme.sistema_controle_assinaturas.application;

import com.acme.sistema_controle_assinaturas.application.dto.CreateSubscriptionCommand;
import com.acme.sistema_controle_assinaturas.application.dto.PaymentWebhookCommand;
import com.acme.sistema_controle_assinaturas.application.dto.SubscriptionResponse;
import com.acme.sistema_controle_assinaturas.domain.exception.DomainNotFoundException;
import com.acme.sistema_controle_assinaturas.domain.model.EventType;
import com.acme.sistema_controle_assinaturas.domain.model.Plan;
import com.acme.sistema_controle_assinaturas.domain.model.Subscription;
import com.acme.sistema_controle_assinaturas.domain.model.events.SubscriptionEventMessage;
import com.acme.sistema_controle_assinaturas.domain.repository.PlanRepository;
import com.acme.sistema_controle_assinaturas.domain.repository.SubscriptionRepository;
import com.acme.sistema_controle_assinaturas.domain.service.BillingDateCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class SubscriptionApplicationService {

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EventLogService eventLogService;
    private final Clock clock;

    public SubscriptionApplicationService(PlanRepository planRepository,
                                          SubscriptionRepository subscriptionRepository,
                                          EventLogService eventLogService,
                                          Clock clock) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.eventLogService = eventLogService;
        this.clock = clock;
    }

    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionCommand command) {
        Plan plan = planRepository.findById(command.planId())
                .orElseThrow(() -> new DomainNotFoundException("Plano não encontrado"));

        LocalDate nextBillingDate = BillingDateCalculator.nextBillingDate(plan.getBillingCycle(), LocalDate.now(clock));

        Subscription subscription = Subscription.create(plan, command.customerEmail(), nextBillingDate, clock);
        subscriptionRepository.save(subscription);

        SubscriptionEventMessage event = SubscriptionEventMessage.subscriptionCreated(
                subscription,
                plan.getPrice(),
                LocalDate.now(clock)
        );
        eventLogService.recordAndPublish(event);

        return SubscriptionResponse.from(subscription);
    }

    @Transactional
    public void enqueuePaymentEvent(PaymentWebhookCommand command) {
        Subscription subscription = subscriptionRepository.findById(command.subscriptionId())
                .orElseThrow(() -> new DomainNotFoundException("Assinatura não encontrada"));

        Plan plan = subscription.getPlan();
        SubscriptionEventMessage event = toEvent(command, subscription, plan);
        eventLogService.recordAndPublish(event);
    }

    private SubscriptionEventMessage toEvent(PaymentWebhookCommand command, Subscription subscription, Plan plan) {
        if (command.eventType() == EventType.PAYMENT_SUCCESS) {
            return SubscriptionEventMessage.paymentSuccess(
                    subscription.getId(),
                    plan.getId(),
                    subscription.getCustomerEmail(),
                    command.amount(),
                    command.eventDate()
            );
        }
        return SubscriptionEventMessage.paymentFailed(
                subscription.getId(),
                plan.getId(),
                subscription.getCustomerEmail(),
                command.amount(),
                command.eventDate()
        );
    }
}
