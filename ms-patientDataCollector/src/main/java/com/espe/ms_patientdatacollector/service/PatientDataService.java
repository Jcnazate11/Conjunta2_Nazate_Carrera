package com.espe.ms_patientdatacollector.service;

import com.espe.ms_patientdatacollector.dto.NewVitalSignEventDTO;
import com.espe.ms_patientdatacollector.dto.VitalSignDTO;
import com.espe.ms_patientdatacollector.entity.VitalSign;
import com.espe.ms_patientdatacollector.repository.VitalSignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientDataService {

    @Autowired
    private VitalSignRepository repository;

    @Autowired
    private EventPublisherService eventPublisherService;

    // Guardar datos de signo vital y emitir evento
    public void saveVitalSign(VitalSignDTO vitalSignDTO) {
        // Validar los datos antes de guardarlos
        validateVitalSign(vitalSignDTO);

        // Convertir DTO a entidad y guardar en la base de datos
        VitalSign vitalSign = new VitalSign();
        vitalSign.setDeviceId(vitalSignDTO.getDeviceId());
        vitalSign.setType(vitalSignDTO.getType());
        vitalSign.setValue(vitalSignDTO.getValue());
        vitalSign.setTimestamp(LocalDateTime.parse(vitalSignDTO.getTimestamp()));

        repository.save(vitalSign);

        // Emitir el evento NewVitalSignEvent
        NewVitalSignEventDTO eventDTO = new NewVitalSignEventDTO(
                "EVT-" + System.currentTimeMillis(),  // Generación simple de un ID único para el evento
                vitalSignDTO.getDeviceId(),
                vitalSignDTO.getType(),
                vitalSignDTO.getValue(),
                vitalSignDTO.getTimestamp()
        );
        eventPublisherService.publishNewVitalSignEvent(eventDTO);
    }

    // Validar que los datos estén dentro de los rangos esperados
    public void validateVitalSign(VitalSignDTO vitalSignDTO) {
        // Validación del deviceId
        if (vitalSignDTO.getDeviceId() == null || vitalSignDTO.getDeviceId().isEmpty()) {
            throw new IllegalArgumentException("El deviceId es obligatorio.");
        }

        // Validación del tipo de signo vital (debe ser uno de los tipos permitidos)
        if (vitalSignDTO.getType() == null || vitalSignDTO.getType().isEmpty()) {
            throw new IllegalArgumentException("El tipo de signo vital es obligatorio.");
        }

        // Validación de los tipos permitidos
        if (!"heart-rate".equals(vitalSignDTO.getType()) && !"blood-pressure".equals(vitalSignDTO.getType()) && !"oxygen-level".equals(vitalSignDTO.getType())) {
            throw new IllegalArgumentException("Tipo de signo vital inválido.");
        }

        // Validación de rango para heart-rate
        if ("heart-rate".equals(vitalSignDTO.getType())) {
            if (vitalSignDTO.getValue() < 30 || vitalSignDTO.getValue() > 200) {
                throw new IllegalArgumentException("Frecuencia cardíaca fuera de rango válido.");
            }
        }

        // Validación de rango para blood-pressure (ejemplo: 90-180)
        if ("blood-pressure".equals(vitalSignDTO.getType())) {
            if (vitalSignDTO.getValue() < 90 || vitalSignDTO.getValue() > 180) {
                throw new IllegalArgumentException("Presión sanguínea fuera de rango válido.");
            }
        }

        // Validación de rango para oxygen-level (ejemplo: 85-100)
        if ("oxygen-level".equals(vitalSignDTO.getType())) {
            if (vitalSignDTO.getValue() < 85 || vitalSignDTO.getValue() > 100) {
                throw new IllegalArgumentException("Nivel de oxígeno fuera de rango válido.");
            }
        }

        // Validación de timestamp (asegurarse de que el formato de la fecha sea correcto)
        try {
            LocalDateTime.parse(vitalSignDTO.getTimestamp());
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de timestamp inválido.");
        }
    }

    // Obtener historial de signos vitales de un dispositivo
    public List<VitalSign> obtenerPorDeviceId(String deviceId) {
        return repository.findByDeviceId(deviceId);
    }
}
