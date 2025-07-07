package com.espe.ms_carenotifier.controller;

import com.espe.ms_carenotifier.dto.AlertDTO;
import com.espe.ms_carenotifier.dto.NotificationResponseDTO;
import com.espe.ms_carenotifier.service.CareNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conjunta/2p/care-notifier")
public class CareNotifierController {

    @Autowired
    private CareNotifierService careNotifierService;

    // POST /alerts: Recibe un evento de alerta y envía una notificación
    @PostMapping("/alerts")
    public ResponseEntity<NotificationResponseDTO> handleAlert(@RequestBody AlertDTO alertDTO) {
        try {
            // Procesar la alerta y enviar la notificación
            NotificationResponseDTO notificationResponse = careNotifierService.sendNotification(alertDTO);

            // Retornar la respuesta con el estado de la notificación
            return ResponseEntity.status(HttpStatus.OK).body(notificationResponse);
        } catch (Exception e) {
            // Manejo de errores generales
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponseDTO(null, "Error", "Error al procesar la alerta: " + e.getMessage()));
        }
    }

    // POST /mock-email: Endpoint simulado para recibir notificaciones por correo
    @PostMapping("/mock-email")
    public ResponseEntity<String> mockEmailNotification(@RequestParam String recipient,
                                                        @RequestParam String subject,
                                                        @RequestParam String body) {
        try {
            careNotifierService.sendEmail(recipient, subject, body);
            return ResponseEntity.ok("Correo simulado enviado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular el correo: " + e.getMessage());
        }
    }

    // POST /mock-sms: Endpoint simulado para recibir notificaciones por SMS
    @PostMapping("/mock-sms")
    public ResponseEntity<String> mockSMSNotification(@RequestParam String phoneNumber,
                                                      @RequestParam String message) {
        try {
            careNotifierService.sendSMS(phoneNumber, message);
            return ResponseEntity.ok("SMS simulado enviado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular el SMS: " + e.getMessage());
        }
    }

    // POST /mock-push: Endpoint simulado para recibir notificaciones push
    @PostMapping("/mock-push")
    public ResponseEntity<String> mockPushNotification(@RequestParam String recipient,
                                                       @RequestParam String message) {
        try {
            careNotifierService.sendPushNotification(recipient, message);
            return ResponseEntity.ok("Push Notification simulada enviada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular la Push Notification: " + e.getMessage());
        }
    }
}
