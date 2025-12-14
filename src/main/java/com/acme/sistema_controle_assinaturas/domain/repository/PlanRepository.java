package com.acme.sistema_controle_assinaturas.domain.repository;

import com.acme.sistema_controle_assinaturas.domain.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
}
