package com.espe.ms_patientdatacollector.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignDTO {
    private String deviceId;
    private String type;
    private Double value;
    private LocalDateTime timestamp; // Cambiado a LocalDateTime para consistencia
}