package com.espe.ms_patientdatacollector.service;

import com.espe.ms_patientdatacollector.dto.NewVitalSignEventDTO;
import com.espe.ms_patientdatacollector.dto.VitalSignDTO;
import com.espe.ms_patientdatacollector.entity.AuditEvent;
import com.espe.ms_patientdatacollector.entity.VitalSign;
import com.espe.ms_patientdatacollector.repository.AuditEventRepository;
import com.espe.ms_patientdatacollector.repository.VitalSignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PatientDataService {

    private static final Logger log = LoggerFactory.getLogger(PatientDataService.class);

    private final VitalSignRepository repository;
    private final EventPublisherService eventPublisherService;
    private final AuditEventRepository auditEventRepository;

    public PatientDataService(VitalSignRepository repository,
                              EventPublisherService eventPublisherService,
                              AuditEventRepository auditEventRepository) {
        this.repository = repository;
        this.eventPublisherService = eventPublisherService;
        this.auditEventRepository = auditEventRepository;
    }

    public VitalSign saveVitalSign(VitalSignDTO vitalSignDTO, String eventId) {
        if (repository.findByEventId(eventId).isPresent()) {
            log.info("Evento duplicado con ID {}. No se procesará.", eventId);
            logAudit(toEventDTO(vitalSignDTO, eventId), false, "Evento duplicado");
            return null;
        }

        validateVitalSign(vitalSignDTO);

        VitalSign vitalSign = new VitalSign();
        vitalSign.setDeviceId(vitalSignDTO.getDeviceId());
        vitalSign.setType(vitalSignDTO.getType());
        vitalSign.setValue(vitalSignDTO.getValue());
        vitalSign.setTimestamp(vitalSignDTO.getTimestamp() != null ? vitalSignDTO.getTimestamp() : LocalDateTime.now());
        vitalSign.setEventId(eventId);

        VitalSign saved = repository.save(vitalSign);

        NewVitalSignEventDTO eventDTO = toEventDTO(vitalSignDTO, eventId);
        eventPublisherService.publishNewVitalSignEvent(eventDTO);
        logAudit(eventDTO, true, "Evento válido y procesado");

        return saved;
    }

    private NewVitalSignEventDTO toEventDTO(VitalSignDTO dto, String eventId) {
        return new NewVitalSignEventDTO(
                eventId,
                dto.getDeviceId(),
                dto.getType(),
                dto.getValue(),
                dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now()
        );
    }

    private void logAudit(NewVitalSignEventDTO event, boolean valid, String reason) {
        AuditEvent audit = new AuditEvent();
        audit.setEventId(event.getEventId());
        audit.setDeviceId(event.getDeviceId());
        audit.setType(event.getType());
        audit.setValue(event.getValue());
        audit.setTimestamp(event.getTimestamp());
        audit.setValid(valid);
        audit.setReason(reason);

        auditEventRepository.save(audit);
    }

    private void validateVitalSign(VitalSignDTO dto) {
        // Aquí puedes agregar validaciones como límites por tipo, por ejemplo:
        if (dto.getDeviceId() == null || dto.getType() == null || dto.getValue() == null) {
            throw new IllegalArgumentException("Datos incompletos en el signo vital");
        }
    }

    public List<VitalSign> getVitalSignsByDeviceId(String deviceId) {
        return repository.findByDeviceId(deviceId);
    }
}