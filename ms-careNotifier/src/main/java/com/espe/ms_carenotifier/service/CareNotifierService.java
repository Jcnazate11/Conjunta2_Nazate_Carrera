package com.espe.ms_carenotifier.service;

import com.espe.ms_carenotifier.dto.AlertDTO;
import com.espe.ms_carenotifier.dto.NotificationDTO;
import com.espe.ms_carenotifier.dto.NotificationResponseDTO;
import com.espe.ms_carenotifier.entity.Notification;
import com.espe.ms_carenotifier.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CareNotifierService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Almacenamiento temporal para alertas de baja prioridad
    private List<AlertDTO> lowPriorityAlerts = new ArrayList<>();

    // Enviar notificación
    public NotificationResponseDTO sendNotification(AlertDTO alertDTO) {
        // Clasificación de la alerta por gravedad
        String severity = getSeverity(alertDTO.getType());

        // Si es de emergencia, se envía inmediatamente
        if ("EMERGENCY".equals(severity)) {
            sendEmergencyNotification(alertDTO);
        } else if ("WARNING".equals(severity)) {
            // Las alertas de advertencia se almacenan y se envían cada 30 minutos
            lowPriorityAlerts.add(alertDTO);
            return new NotificationResponseDTO(null, alertDTO.getDeviceId(), "PENDIENTE", "Alerta de advertencia registrada.");
        } else {
            sendInformativeNotification(alertDTO);
        }

        // Guardar la notificación en la base de datos
        Notification notification = new Notification();
        notification.setEventType(alertDTO.getType());
        notification.setRecipient("doctor@example.com"); // Ejemplo de destinatario
        notification.setStatus("ENVIADA");
        notification.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notification);

        return new NotificationResponseDTO(notification.getNotificationId(), "doctor@example.com", "ENVIADA", "Notificación enviada correctamente");
    }

    private String getSeverity(String alertType) {
        // Clasificación de gravedad de la alerta
        if (alertType.contains("Critical")) {
            return "EMERGENCY";
        } else if (alertType.contains("Low")) {
            return "WARNING";
        }
        return "INFO";
    }

    private void sendEmergencyNotification(AlertDTO alertDTO) {
        // Lógica para enviar notificación de emergencia
        System.out.println("Enviando notificación de emergencia a: doctor@example.com");
        System.out.println("Alerta: " + alertDTO.getType() + " con valor: " + alertDTO.getValue());
    }

    private void sendInformativeNotification(AlertDTO alertDTO) {
        // Lógica para enviar notificación informativa
        System.out.println("Enviando notificación informativa a: doctor@example.com");
        System.out.println("Alerta: " + alertDTO.getType() + " con valor: " + alertDTO.getValue());
    }

    // Tarea programada que envía las alertas de baja prioridad cada 30 minutos
    @Scheduled(fixedRate = 1800000)  // Ejecuta cada 30 minutos
    public void sendLowPriorityAlerts() {
        if (!lowPriorityAlerts.isEmpty()) {
            System.out.println("Enviando alertas de baja prioridad...");
            for (AlertDTO alert : lowPriorityAlerts) {
                sendInformativeNotification(alert);  // Simulación de envío
            }
            lowPriorityAlerts.clear();  // Limpiar la lista después de enviarlas
        }
    }

    // Método para simular el envío de correo
    public void sendEmail(String recipient, String subject, String body) {
        // Lógica para enviar correo (simulada aquí como un log)
        System.out.println("Enviando correo a: " + recipient);
        System.out.println("Asunto: " + subject);
        System.out.println("Cuerpo: " + body);
    }

    // Método para simular el envío de SMS
    public void sendSMS(String phoneNumber, String message) {
        // Lógica para enviar SMS (simulada aquí como un log)
        System.out.println("Enviando SMS a: " + phoneNumber);
        System.out.println("Mensaje: " + message);
    }

    // Método para simular el envío de Push Notification
    public void sendPushNotification(String recipient, String message) {
        // Lógica para enviar Push Notification (simulada aquí como un log)
        System.out.println("Enviando Push Notification a: " + recipient);
        System.out.println("Mensaje: " + message);
    }
}
