package com.acme.sistema_controle_assinaturas.infrastructure.api;

import com.acme.sistema_controle_assinaturas.application.SubscriptionApplicationService;
import com.acme.sistema_controle_assinaturas.infrastructure.api.requests.PaymentWebhookRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final SubscriptionApplicationService subscriptionApplicationService;

    public WebhookController(SubscriptionApplicationService subscriptionApplicationService) {
        this.subscriptionApplicationService = subscriptionApplicationService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> handlePayment(@Valid @RequestBody PaymentWebhookRequest request) {
        subscriptionApplicationService.enqueuePaymentEvent(request.toCommand());
        return ResponseEntity.accepted().build();
    }
}
