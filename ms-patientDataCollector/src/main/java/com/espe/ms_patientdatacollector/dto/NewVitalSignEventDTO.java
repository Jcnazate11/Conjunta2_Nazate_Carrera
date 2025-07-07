package com.espe.ms_patientdatacollector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewVitalSignEventDTO {

    private String eventId;      // Identificador único del evento
    private String deviceId;     // ID del dispositivo médico
    private String type;         // Tipo de signo vital
    private Double value;        // Valor del signo vital
    private String timestamp;    // Marca de tiempo
}