package com.espe.ms_patientdatacollector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignDTO {

    private String deviceId;     // Identificador del dispositivo médico
    private String type;         // Tipo de signo vital (ej. heart-rate, blood-pressure)
    private Double value;        // Valor del signo vital
    private String timestamp;    // Marca de tiempo de la lectura (en formato ISO-8601)
}