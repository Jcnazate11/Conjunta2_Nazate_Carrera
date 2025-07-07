package com.espe.ms_patientdatacollector.controller;

import com.espe.ms_patientdatacollector.dto.VitalSignDTO;
import com.espe.ms_patientdatacollector.entity.VitalSign;
import com.espe.ms_patientdatacollector.service.PatientDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conjunta/2p/patient-data")
public class PatientDataController {

    @Autowired
    private PatientDataService service;

    //POST /vital-signs: Recibe los datos de los dispositivos médicos
    @PostMapping("/vital-signs")
    public ResponseEntity<String> recibirDatosVitales(@RequestBody VitalSignDTO vitalSignDTO) {
        try {
            // Validación manual de los datos entrantes
            if (vitalSignDTO.getDeviceId() == null || vitalSignDTO.getDeviceId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("deviceId es obligatorio.");
            }

            if (vitalSignDTO.getType() == null || vitalSignDTO.getType().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El tipo de signo vital es obligatorio.");
            }

            if (vitalSignDTO.getValue() == null || vitalSignDTO.getValue() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El valor del signo vital debe ser positivo.");
            }

            // Validar el tipo de signo vital
            if (!"heart-rate".equals(vitalSignDTO.getType()) && !"blood-pressure".equals(vitalSignDTO.getType()) && !"oxygen-level".equals(vitalSignDTO.getType())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de signo vital inválido.");
            }

            // Validar que el valor esté en el rango permitido
            if ("heart-rate".equals(vitalSignDTO.getType())) {
                if (vitalSignDTO.getValue() < 30 || vitalSignDTO.getValue() > 200) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Frecuencia cardíaca fuera de rango válido.");
                }
            }

            service.saveVitalSign(vitalSignDTO);  // Guardamos los datos y emitimos el evento
            return ResponseEntity.ok("Datos de signo vital recibidos y procesados correctamente.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Datos fuera de rango o inválidos.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno en el servidor.");
        }
    }

    //GET /vital-signs/{deviceId}: Obtiene el historial de signos vitales por dispositivo
    @GetMapping("/vital-signs/{deviceId}")
    public ResponseEntity<List<VitalSign>> obtenerHistorial(@PathVariable String deviceId) {
        try {
            List<VitalSign> historial = service.obtenerPorDeviceId(deviceId);
            if (historial.isEmpty()) {
                return ResponseEntity.notFound().build();  // No se encuentran registros
            }
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Error al procesar la solicitud
        }
    }
}
