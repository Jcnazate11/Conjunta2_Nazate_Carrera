package com.espe.ms_patientdatacollector.controller;



import com.espe.ms_patientdatacollector.dto.VitalSignDTO;
import com.espe.ms_patientdatacollector.entity.VitalSign;
import com.espe.ms_patientdatacollector.service.PatientDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vital-signs")
public class PatientDataController {

    private final PatientDataService service;

    public PatientDataController(PatientDataService service) {
        this.service = service;
    }

    /**
     * Endpoint para recibir un nuevo evento de signo vital con soporte de idempotencia.
     * El eventId es requerido en la cabecera para identificar de forma única el evento.
     */
    @PostMapping
    public ResponseEntity<String> recibirDatosVitales(
            @RequestBody VitalSignDTO vitalSignDTO,
            @RequestHeader("X-Event-ID") String eventId) {
        try {
            var result = service.saveVitalSign(vitalSignDTO, eventId);
            if (result == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Evento duplicado: ya fue procesado.");
            }
            return ResponseEntity.ok("Datos de signo vital recibidos y procesados correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno en el servidor.");
        }
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<List<VitalSign>> obtenerHistorial(@PathVariable String deviceId) {
        try {
            List<VitalSign> historial = service.getVitalSignsByDeviceId(deviceId);
            return historial.isEmpty()
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}