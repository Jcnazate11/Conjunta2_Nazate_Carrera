package com.espe.ms_patientdatacollector.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String deviceId;
    private String type;
    private Double value;
    private LocalDateTime timestamp;

    @Column(unique = true)
    private String eventId;
}
