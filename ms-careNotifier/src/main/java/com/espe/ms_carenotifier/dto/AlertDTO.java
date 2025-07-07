package com.espe.ms_carenotifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertDTO {

    private String eventId;       // Identificador único del evento
    private String deviceId;      // Dispositivo que generó la alerta
    private String type;          // Tipo de alerta (ej. CriticalHeartRateAlert)
    private Double value;         // Valor de la alerta (ej. valor de frecuencia cardíaca)
    private String timestamp;     // Marca de tiempo de la alerta (formato ISO-8601)
}
