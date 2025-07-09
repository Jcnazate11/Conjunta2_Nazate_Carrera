package com.espe.ms_carenotifier.service;

import com.espe.ms_carenotifier.dto.AlertDTO;
import com.espe.ms_carenotifier.dto.NotificationResponseDTO;
import com.espe.ms_carenotifier.entity.Notification;
import com.espe.ms_carenotifier.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CareNotifierService {

    private static final Logger log = LoggerFactory.getLogger(CareNotifierService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    // Buffer para alertas de baja prioridad
    private final List<AlertDTO> lowPriorityAlerts = new ArrayList<>();

    /**
     * Recibe una alerta y envía la notificación apropiada.
     */
    public NotificationResponseDTO sendNotification(AlertDTO alertDTO) {
        String severity = classifySeverity(alertDTO.getType());

        switch (severity) {
            case "EMERGENCY" -> {
                log.info("🚨 Emergencia: enviando notificación inmediata");
                sendAllChannels(alertDTO);
                return persistAndRespond(alertDTO, "ENVIADA", "Notificación crítica enviada");
            }
            case "WARNING" -> {
                log.info("⚠️ Advertencia: almacenando para envío agrupado");
                lowPriorityAlerts.add(alertDTO);
                return new NotificationResponseDTO(null, "pendiente", "PENDIENTE", "Se enviará en lote cada 30 min");
            }
            default -> {
                log.info("ℹ️ Info: enviando notificación informativa");
                sendAllChannels(alertDTO);
                return persistAndRespond(alertDTO, "ENVIADA", "Notificación informativa enviada");
            }
        }
    }

    /**
     * Tarea programada: envía notificaciones de baja prioridad acumuladas cada 30 min.
     */
    @Scheduled(fixedRate = 1800000) // 30 min
    public void sendLowPriorityAlerts() {
        if (!lowPriorityAlerts.isEmpty()) {
            log.info("📤 Enviando {} alertas de baja prioridad...", lowPriorityAlerts.size());
            for (AlertDTO alert : lowPriorityAlerts) {
                sendAllChannels(alert);
                persistAndRespond(alert, "ENVIADA", "Enviada desde buffer");
            }
            lowPriorityAlerts.clear();
        }
    }

    /**
     * Simula el envío de notificaciones por todos los canales.
     */
    private void sendAllChannels(AlertDTO alert) {
        sendEmail("doctor@hospital.com", "Alerta médica: " + alert.getType(), alert.toString());
        sendSMS("+593999999999", alert.toString());
        sendPushNotification("medico-123", alert.toString());
    }

    private NotificationResponseDTO persistAndRespond(AlertDTO alert, String status, String message) {
        Notification n = new Notification();
        n.setEventType(alert.getType());
        n.setRecipient("doctor@hospital.com");
        n.setStatus(status);
        n.setTimestamp(LocalDateTime.now());
        notificationRepository.save(n);

        return new NotificationResponseDTO(n.getNotificationId(), n.getRecipient(), status, message);
    }

    private String classifySeverity(String type) {
        return switch (type) {
            case "CriticalHeartRateAlert", "OxygenLevelCritical" -> "EMERGENCY";
            case "DeviceOfflineAlert" -> "WARNING";
            default -> "INFO";
        };
    }

    // Simulaciones de canales

    public void sendEmail(String recipient, String subject, String body) {
        System.out.println("📧 Correo a: " + recipient);
        System.out.println("Asunto: " + subject);
        System.out.println("Cuerpo: " + body);
    }

    public void sendSMS(String phoneNumber, String message) {
        System.out.println("📲 SMS a: " + phoneNumber);
        System.out.println("Mensaje: " + message);
    }

    public void sendPushNotification(String recipient, String message) {
        System.out.println("🔔 Push Notification a: " + recipient);
        System.out.println("Mensaje: " + message);
    }
}
