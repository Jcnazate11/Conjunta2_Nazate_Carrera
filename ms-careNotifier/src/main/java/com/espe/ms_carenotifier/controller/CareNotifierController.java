package com.espe.ms_carenotifier.controller;

import com.espe.ms_carenotifier.dto.*;
import com.espe.ms_carenotifier.service.CareNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/care-notifier")
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
                    .body(new NotificationResponseDTO("ERROR", "Error al procesar la alerta: " + e.getMessage()));

        }
    }

    // POST /mock-email: Endpoint simulado para recibir notificaciones por correo
    // POST /mock-email: Simulación de envío de correo vía JSON
    @PostMapping("/mock-email")
    public ResponseEntity<String> mockEmailNotification(@RequestBody EmailRequestDTO emailRequest) {
        try {
            careNotifierService.sendEmail(emailRequest.getRecipient(), emailRequest.getSubject(), emailRequest.getBody());
            return ResponseEntity.ok("Correo simulado enviado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular el correo: " + e.getMessage());
        }
    }


    // POST /mock-sms: Simulación de SMS vía JSON
    @PostMapping("/mock-sms")
    public ResponseEntity<String> mockSMSNotification(@RequestBody SMSRequestDTO smsRequest) {
        try {
            careNotifierService.sendSMS(smsRequest.getPhoneNumber(), smsRequest.getMessage());
            return ResponseEntity.ok("SMS simulado enviado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular el SMS: " + e.getMessage());
        }
    }

    // POST /mock-push: Endpoint simulado para recibir notificaciones push
    @PostMapping("/mock-push")
    public ResponseEntity<String> mockPushNotification(@RequestBody PushNotificationRequestDTO pushRequest) {
        try {
            careNotifierService.sendPushNotification(pushRequest.getRecipient(), pushRequest.getMessage());
            return ResponseEntity.ok("Push Notification simulada enviada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular la Push Notification: " + e.getMessage());
        }
    }
}
