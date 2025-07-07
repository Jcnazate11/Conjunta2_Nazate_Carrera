package com.espe.ms_healtAnalyzer.controller;

import com.espe.ms_healtAnalyzer.entity.VitalSign;
import com.espe.ms_healtAnalyzer.repository.VitalSignRepository;
import com.espe.ms_healtAnalyzer.service.HealthAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/health-analyzer")
public class HealthAnalyzerController {

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private HealthAnalyzerService healthAnalyzerService;

    @Autowired(required = false)

    // Endpoint para consultar el historial de signos vitales por dispositivo
    @GetMapping("/vital-signs/{deviceId}")
    public List<VitalSign> getVitalSignsByDevice(@PathVariable String deviceId) {
        // Devuelve los signos vitales de los últimos 30 días como ejemplo
        return vitalSignRepository.findByDeviceIdAndTimestampAfter(deviceId, LocalDateTime.now().minusDays(30));
    }

    // Endpoint para forzar el análisis manual de un evento (opcional, para pruebas)
    @PostMapping("/analyze")
    public String analyzeVitalSign(@RequestBody String eventData) {
        // Aquí podrías deserializar eventData a NewVitalSignEvent y pasarlo a healthAnalyzerService
        // Por simplicidad, solo registramos que se recibió
        healthAnalyzerService.processVitalSign(null); // Implementa la deserialización si lo necesitas
        return "Análisis iniciado para evento recibido";
    }


}