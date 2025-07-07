package com.espe.ms_patientdatacollector.listener;

import com.espe.ms_patientdatacollector.dto.NewVitalSignEventDTO;
import com.espe.ms_patientdatacollector.dto.VitalSignDTO;
import com.espe.ms_patientdatacollector.service.PatientDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NewVitalSignEventListener {

    private static final Logger log = LoggerFactory.getLogger(NewVitalSignEventListener.class);
    private final PatientDataService patientDataService;

    public NewVitalSignEventListener(PatientDataService patientDataService) {
        this.patientDataService = patientDataService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.new-vital-sign}")
    public void handleNewVitalSignEvent(NewVitalSignEventDTO event) {
        try {
            log.info("Procesando evento del dispositivo: {}", event.getDeviceId());

            // Validación básica de rangos (requisito B.1.e)
            if ("heart-rate".equals(event.getType())) {
                if (event.getValue() < 30 || event.getValue() > 200) {
                    throw new IllegalArgumentException("Frecuencia cardíaca fuera de rango válido");
                }
            }

            // Convertir NewVitalSignEventDTO a VitalSignDTO
            VitalSignDTO vitalSignDTO = new VitalSignDTO(
                    event.getDeviceId(),
                    event.getType(),
                    event.getValue(),
                    event.getTimestamp()
            );

            // Persistir en CockroachDB
            patientDataService.saveVitalSign(vitalSignDTO);

        } catch (Exception ex) {
            log.error("Error procesando evento {}: {}", event.getEventId(), ex.getMessage());
            throw ex; // Re-lanzar para manejo por RabbitMQ
        }
    }
}
