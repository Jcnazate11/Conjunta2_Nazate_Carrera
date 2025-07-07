package com.espe.ms_carenotifier.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;  // Identificador único de la notificación

    private String eventType;     // Tipo de evento (ej. CriticalHeartRateAlert, OxygenLevelCritical)
    private String recipient;     // Destinatario de la notificación (ej. médico, enfermera)
    private String status;        // Estado de la notificación (ej. ENVIADA, PENDIENTE, ERROR)
    private LocalDateTime timestamp;  // Fecha y hora de envío de la notificación

    // Constructor, getters y setters generados automáticamente por Lombok
}
