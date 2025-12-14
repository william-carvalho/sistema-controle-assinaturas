package com.acme.sistema_controle_assinaturas.application;

import com.acme.sistema_controle_assinaturas.application.dto.PlanMetric;
import com.acme.sistema_controle_assinaturas.application.dto.SubscriptionMetricsResponse;
import com.acme.sistema_controle_assinaturas.domain.model.SubscriptionStatus;
import com.acme.sistema_controle_assinaturas.domain.repository.PlanRepository;
import com.acme.sistema_controle_assinaturas.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportingService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

    public ReportingService(SubscriptionRepository subscriptionRepository, PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
    }

    @Transactional(readOnly = true)
    public SubscriptionMetricsResponse subscriptionMetrics() {
        long totalActive = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
        long totalCancelled = subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED);

        List<PlanMetric> planMetrics = planRepository.findAll().stream()
                .map(plan -> new PlanMetric(
                        plan.getId(),
                        plan.getName(),
                        subscriptionRepository.countByPlanAndStatus(plan.getId(), SubscriptionStatus.ACTIVE)
                ))
                .toList();

        return new SubscriptionMetricsResponse(totalActive, totalCancelled, planMetrics);
    }
}
