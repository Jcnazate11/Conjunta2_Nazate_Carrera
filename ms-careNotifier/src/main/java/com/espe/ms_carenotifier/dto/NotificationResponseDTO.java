package com.espe.ms_carenotifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {

    private Long notificationId;   // Identificador de la notificación
    private String recipient;      // Destinatario
    private String status;         // Estado de la notificación (ej. ENVIADA, ERROR)
    private String message;        // Mensaje sobre el resultado del envío de la notificación

    public NotificationResponseDTO(Object o, String error, String s) {
    }
}
