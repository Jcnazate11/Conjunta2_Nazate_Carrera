package com.espe.ms_patientdatacollector.entity;

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
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;    // Identificador del dispositivo médico
    private String type;        // Tipo de signo vital (ej. "heart-rate", "blood-pressure", etc.)
    private Double value;       // Valor del signo vital
    private LocalDateTime timestamp; // Marca de tiempo de la lectura del signo vital
}
