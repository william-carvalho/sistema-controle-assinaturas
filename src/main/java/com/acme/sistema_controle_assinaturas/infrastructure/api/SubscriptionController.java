package com.acme.sistema_controle_assinaturas.infrastructure.api;

import com.acme.sistema_controle_assinaturas.application.SubscriptionApplicationService;
import com.acme.sistema_controle_assinaturas.application.dto.SubscriptionResponse;
import com.acme.sistema_controle_assinaturas.infrastructure.api.requests.CreateSubscriptionRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionApplicationService subscriptionApplicationService;

    public SubscriptionController(SubscriptionApplicationService subscriptionApplicationService) {
        this.subscriptionApplicationService = subscriptionApplicationService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse response = subscriptionApplicationService.createSubscription(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
