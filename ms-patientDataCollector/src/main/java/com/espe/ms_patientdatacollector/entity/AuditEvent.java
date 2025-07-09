package com.espe.ms_patientdatacollector.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String eventId;
    private String deviceId;
    private String type;
    private Double value;
    private LocalDateTime timestamp;

    private boolean valid;       // true: válido, false: rechazado o duplicado
    private String reason;       // razón de rechazo o mensaje de éxito

    private LocalDateTime receivedAt = LocalDateTime.now();
}
