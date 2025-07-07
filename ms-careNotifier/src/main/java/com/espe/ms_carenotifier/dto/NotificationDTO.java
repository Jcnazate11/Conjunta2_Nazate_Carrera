package com.espe.ms_carenotifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private String eventType;     // Tipo de evento (ej. CriticalHeartRateAlert, OxygenLevelCritical)
    private String recipient;     // Destinatario de la notificación (ej. médico, enfermera)
    private String status;        // Estado de la notificación (ej. ENVIADA, PENDIENTE, ERROR)
    private String timestamp;     // Fecha y hora de la notificación (en formato ISO-8601)
}
