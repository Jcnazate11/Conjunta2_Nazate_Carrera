package com.espe.ms_patientdatacollector.repository;

import com.espe.ms_patientdatacollector.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {

    // Obtener historial de signos vitales de un dispositivo específico
    List<VitalSign> findByDeviceId(String deviceId);
    Optional<VitalSign> findByEventId(String eventId);




}
