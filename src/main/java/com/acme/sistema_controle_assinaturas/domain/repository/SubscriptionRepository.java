package com.acme.sistema_controle_assinaturas.domain.repository;

import com.acme.sistema_controle_assinaturas.domain.model.Subscription;
import com.acme.sistema_controle_assinaturas.domain.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    long countByStatus(SubscriptionStatus status);

    @Query("select count(s) from Subscription s where s.plan.id = :planId and s.status = :status")
    long countByPlanAndStatus(@Param("planId") UUID planId, @Param("status") SubscriptionStatus status);

    List<Subscription> findByPlanId(UUID planId);
}
