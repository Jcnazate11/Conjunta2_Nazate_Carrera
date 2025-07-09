package com.espe.ms_healtAnalyzer.service;

import com.espe.ms_healtAnalyzer.dto.AlertEvent;
import com.espe.ms_healtAnalyzer.dto.NewVitalSignEvent;
import com.espe.ms_healtAnalyzer.entity.MedicalAlert;
import com.espe.ms_healtAnalyzer.entity.VitalSign;
import com.espe.ms_healtAnalyzer.repository.MedicalAlertRespository;
import com.espe.ms_healtAnalyzer.repository.VitalSignRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class HealthAnalyzerService {

    private static final Logger logger = LoggerFactory.getLogger(HealthAnalyzerService.class);

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private MedicalAlertRespository medicalAlertRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "vital-sign-queue")
    @Transactional
    public void processVitalSign(NewVitalSignEvent event) {
        logger.info("📥 Evento recibido: {}", event);

        if (!isValidEvent(event)) {
            logger.warn("⚠️ Evento inválido descartado: {}", event);
            return;
        }

        // Guardar el signo vital
        VitalSign vitalSign = new VitalSign();
        vitalSign.setDeviceId(event.getDeviceId());
        vitalSign.setType(event.getType());
        vitalSign.setValue(event.getValue());
        vitalSign.setTimestamp(event.getTimestamp());
        vitalSignRepository.save(vitalSign);

        // Analizar el evento para detectar anomalías
        analyzeVitalSign(event);
    }

    private boolean isValidEvent(NewVitalSignEvent event) {
        return event.getDeviceId() != null
                && event.getType() != null
                && event.getTimestamp() != null
                && (!event.getType().equals("heart-rate")
                || (event.getValue() >= 30 && event.getValue() <= 200));
    }

    private void analyzeVitalSign(NewVitalSignEvent event) {
        String type = event.getType();
        double value = event.getValue();
        String deviceId = event.getDeviceId();
        LocalDateTime timestamp = event.getTimestamp();

        switch (type) {
            case "heart-rate":
            case "heartRate":  // Compatibilidad
                if (value > 140 || value < 40) {
                    generateAlert("CriticalHeartRateAlert", deviceId, value, value > 140 ? 140 : 40, timestamp);
                }
                break;

            case "oxygen":
                if (value < 90) {
                    generateAlert("OxygenLevelCritical", deviceId, value, 90, timestamp);
                }
                break;

            case "blood-pressure-systolic":
            case "systolicPressure":
                if (value > 180) {
                    generateAlert("BloodPressureCritical", deviceId, value, 180, timestamp);
                }
                break;

            case "blood-pressure-diastolic":
            case "diastolicPressure":
                if (value > 120) {
                    generateAlert("BloodPressureCritical", deviceId, value, 120, timestamp);
                }
                break;

            default:
                logger.info("ℹ️ Tipo de signo vital no monitoreado para alertas: {}", type);
        }
    }

    private void generateAlert(String type, String deviceId, double value, double threshold, LocalDateTime timestamp) {
        String alertId = "ALT-" + UUID.randomUUID();
        String routingKey;

        // Crear evento para RabbitMQ
        AlertEvent alert = new AlertEvent(alertId, type, deviceId, value, threshold, timestamp);

        // Guardar alerta médica en la BD
        MedicalAlert medicalAlert = new MedicalAlert();
        medicalAlert.setAlertId(alertId);
        medicalAlert.setType(type);
        medicalAlert.setDeviceId(deviceId);
        medicalAlert.setValue(value);
        medicalAlert.setThreshold(threshold);
        medicalAlert.setTimestamp(timestamp);
        medicalAlertRepository.save(medicalAlert);

        try {
            switch (type) {
                case "CriticalHeartRateAlert":
                    routingKey = "alerts.heart-rate";
                    break;
                case "OxygenLevelCritical":
                    routingKey = "alerts.oxygen";
                    break;
                case "BloodPressureCritical":
                    routingKey = "alerts.pressure";
                    break;
                case "DeviceOfflineAlert":
                    routingKey = "alerts.device-offline";
                    break;
                default:
                    routingKey = "alerts.unknown";
            }

            rabbitTemplate.convertAndSend("alerts-exchange", routingKey, alert);
            logger.info("🚨 Alerta enviada: {}", alert);
        } catch (Exception e) {
            logger.error("❌ Error al enviar alerta a RabbitMQ: {}", e.getMessage());
            storeAlertLocally(alert);
        }
    }

    private void storeAlertLocally(AlertEvent alert) {
        // Simulación de fallback local (ejemplo)
        logger.warn("🔁 Alerta almacenada localmente (simulado): {}", alert);
    }
}
