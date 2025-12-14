package com.acme.sistema_controle_assinaturas.infrastructure.api;

import com.acme.sistema_controle_assinaturas.application.ReportingService;
import com.acme.sistema_controle_assinaturas.application.dto.SubscriptionMetricsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportingService reportingService;

    public ReportController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/subscriptions")
    public SubscriptionMetricsResponse metrics() {
        return reportingService.subscriptionMetrics();
    }
}
