package com.espe.ms_patientdatacollector.repository;

import com.espe.ms_patientdatacollector.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
}