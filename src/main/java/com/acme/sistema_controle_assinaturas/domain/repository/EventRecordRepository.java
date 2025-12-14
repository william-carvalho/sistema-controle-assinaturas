package com.acme.sistema_controle_assinaturas.domain.repository;

import com.acme.sistema_controle_assinaturas.domain.model.EventRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRecordRepository extends JpaRepository<EventRecord, UUID> {
}
