package com.espe.ms_healtAnalyzer.service;


import com.espe.ms_healtAnalyzer.dto.AlertEvent;
import com.espe.ms_healtAnalyzer.entity.MedicalAlert;
import com.espe.ms_healtAnalyzer.repository.MedicalAlertRespository;
import com.espe.ms_healtAnalyzer.repository.VitalSignRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceMonitorService {

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private MedicalAlertRespository medicalAlertRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 🕒 Ejecutar cada 6 horas
    @Scheduled(fixedRate = 21600000) // 6 * 60 * 60 * 1000 ms
    public void checkInactiveDevices() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<String> inactiveDevices = vitalSignRepository.findInactiveDevicesSince(threshold);

        for (String deviceId : inactiveDevices) {
            String alertId = "ALT-" + UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            // Crear alerta
            AlertEvent alert = new AlertEvent(
                    alertId,
                    "DeviceOfflineAlert",
                    deviceId,
                    0.0,
                    0.0,
                    now
            );

            // Guardar alerta en la base de datos
            MedicalAlert medicalAlert = new MedicalAlert();
            medicalAlert.setAlertId(alertId);
            medicalAlert.setType("DeviceOfflineAlert");
            medicalAlert.setDeviceId(deviceId);
            medicalAlert.setValue(0.0);
            medicalAlert.setThreshold(0.0);
            medicalAlert.setTimestamp(now);
            medicalAlertRepository.save(medicalAlert);

            // Enviar alerta a RabbitMQ
            rabbitTemplate.convertAndSend("alerts-exchange", "alerts.critical", alert);
        }
    }
}

