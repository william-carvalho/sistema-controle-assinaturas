package com.acme.sistema_controle_assinaturas.infrastructure.api;

import com.acme.sistema_controle_assinaturas.domain.model.BillingCycle;
import com.acme.sistema_controle_assinaturas.domain.model.Plan;
import com.acme.sistema_controle_assinaturas.domain.model.Subscription;
import com.acme.sistema_controle_assinaturas.domain.repository.PlanRepository;
import com.acme.sistema_controle_assinaturas.domain.repository.SubscriptionRepository;
import com.acme.sistema_controle_assinaturas.support.TestClockConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestClockConfig.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        subscriptionRepository.deleteAll();
        planRepository.deleteAll();

        Plan basic = planRepository.save(Plan.of("Basic Plan", BigDecimal.valueOf(20.00), BillingCycle.MONTHLY));
        Plan premium = planRepository.save(Plan.of("Premium Plan", BigDecimal.valueOf(50.00), BillingCycle.MONTHLY));

        Subscription active = Subscription.create(basic, "active@example.com", LocalDate.now(clock).plusMonths(1), clock);
        active.activate(LocalDate.now(clock).plusMonths(1));
        subscriptionRepository.save(active);

        Subscription cancelled = Subscription.create(basic, "cancelled@example.com", LocalDate.now(clock).plusMonths(1), clock);
        cancelled.cancel();
        subscriptionRepository.save(cancelled);

        Subscription premiumActive = Subscription.create(premium, "premium@example.com", LocalDate.now(clock).plusMonths(1), clock);
        premiumActive.activate(LocalDate.now(clock).plusMonths(1));
        subscriptionRepository.save(premiumActive);
    }

    @Test
    void shouldReturnSubscriptionMetrics() throws Exception {
        mockMvc.perform(get("/api/reports/subscriptions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_active").value(2))
                .andExpect(jsonPath("$.total_cancelled").value(1))
                .andExpect(jsonPath("$.plans[?(@.name=='Basic Plan')].active_subscriptions", contains(1)))
                .andExpect(jsonPath("$.plans[?(@.name=='Premium Plan')].active_subscriptions", contains(1)));
    }
}
