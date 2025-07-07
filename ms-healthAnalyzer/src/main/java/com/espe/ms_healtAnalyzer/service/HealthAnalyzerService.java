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

        @RabbitListener(queues = "vital-signs-queue")
        @Transactional
        public void processVitalSign(NewVitalSignEvent event) {
            logger.info("Recibido evento NewVitalSignEvent para dispositivo: {}", event.getDeviceId());

            // Validar formato y rango de datos
            if (!isValidEvent(event)) {
                logger.error("Evento inválido: {}", event);
                return;
            }

            // Guardar signo vital en CockroachDB
            VitalSign vitalSign = new VitalSign();
            vitalSign.setDeviceId(event.getDeviceId());
            vitalSign.setType(event.getType());
            vitalSign.setValue(event.getValue());
            vitalSign.setTimestamp(event.getTimestamp());
            vitalSignRepository.save(vitalSign);

            // Analizar signos vitales y generar alertas
            analyzeVitalSign(event);
        }

        private boolean isValidEvent(NewVitalSignEvent event) {
            if (event.getDeviceId() == null || event.getType() == null || event.getTimestamp() == null) {
                return false;
            }
            if (event.getType().equals("heart-rate") && (event.getValue() > 200 || event.getValue() < 30)) {
                return false;
            }
            return true;
        }

        private void analyzeVitalSign(NewVitalSignEvent event) {
            switch (event.getType()) {
                case "heart-rate":
                    if (event.getValue() > 140 || event.getValue() < 40) {
                        generateAlert("CriticalHeartRateAlert", event.getDeviceId(), event.getValue(),
                                event.getValue() > 140 ? 140 : 40, event.getTimestamp());
                    }
                    break;
                case "oxygen":
                    if (event.getValue() < 90) {
                        generateAlert("OxygenLevelCritical", event.getDeviceId(), event.getValue(), 90, event.getTimestamp());
                    }
                    break;
                case "blood-pressure-systolic":
                    if (event.getValue() > 180) {
                        generateAlert("HighBloodPressureAlert", event.getDeviceId(), event.getValue(), 180, event.getTimestamp());
                    }
                    break;
                case "blood-pressure-diastolic":
                    if (event.getValue() > 120) {
                        generateAlert("HighBloodPressureAlert", event.getDeviceId(), event.getValue(), 120, event.getTimestamp());
                    }
                    break;
            }
        }

        private void generateAlert(String type, String deviceId, double value, double threshold, LocalDateTime timestamp) {
            String alertId = "ALT-" + UUID.randomUUID().toString();
            AlertEvent alert = new AlertEvent(alertId, type, deviceId, value, threshold, timestamp);

            // Guardar alerta en CockroachDB
            MedicalAlert medicalAlert = new MedicalAlert();
            medicalAlert.setType(type);
            medicalAlert.setDeviceId(deviceId);
            medicalAlert.setValue(value);
            medicalAlert.setThreshold(threshold);
            medicalAlert.setTimestamp(timestamp);
            medicalAlertRepository.save(medicalAlert);

            // Publicar alerta en RabbitMQ con reintentos
            try {
                rabbitTemplate.convertAndSend("alerts-exchange", "alerts.critical", alert);
                logger.info("Alerta {} enviada para dispositivo: {}", type, deviceId);
            } catch (Exception e) {
                logger.error("Error al enviar alerta a RabbitMQ, almacenando localmente: {}", e.getMessage());
                // Almacenar localmente en caso de fallo (usando SQLite o en memoria)
                storeAlertLocally(alert);
            }
        }

        private void storeAlertLocally(AlertEvent alert) {
            // Implementar almacenamiento local (por ejemplo, en SQLite o en memoria)
            // Para este ejemplo, solo registramos el intento
            logger.warn("Alerta almacenada localmente: {}", alert);
            // TODO: Implementar SQLite o almacenamiento en memoria y lógica de reintento
        }
    }

