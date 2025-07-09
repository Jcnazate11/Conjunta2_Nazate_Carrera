package com.espe.ms_healtAnalyzer.controller;

import com.espe.ms_healtAnalyzer.dto.RawVitalSignEventDTO;
import com.espe.ms_healtAnalyzer.dto.NewVitalSignEvent;
import com.espe.ms_healtAnalyzer.entity.VitalSign;
import com.espe.ms_healtAnalyzer.repository.VitalSignRepository;
import com.espe.ms_healtAnalyzer.service.HealthAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/health-analyzer")  // ✅ Cambio importante para que coincida con el StripPrefix=2
public class HealthAnalyzerController {

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private HealthAnalyzerService healthAnalyzerService;

    @GetMapping("/vital-signs/{deviceId}")
    public List<VitalSign> getVitalSignsByDevice(@PathVariable String deviceId) {
        return vitalSignRepository.findByDeviceIdAndTimestampAfter(deviceId, LocalDateTime.now().minusDays(30));
    }

    @PostMapping("/analyze")
    public String analyzeVitalSign(@RequestBody RawVitalSignEventDTO input) {
        try {
            String[] parts = input.getEventData().split("-");
            if (parts.length < 7) {
                return "Formato inválido del evento. Se esperaba: deviceId-type-value-fecha-hora";
            }

            NewVitalSignEvent event = new NewVitalSignEvent();
            event.setDeviceId(parts[0]);
            event.setType(parts[1]);
            event.setValue(Double.parseDouble(parts[2]));
            String dateTimeString = parts[3] + "-" + parts[4] + "-" + parts[5] + "T" + parts[6];
            event.setTimestamp(LocalDateTime.parse(dateTimeString));


            healthAnalyzerService.processVitalSign(event);
            return "Análisis iniciado para el dispositivo: " + event.getDeviceId();

        } catch (Exception e) {
            return "Error al procesar evento: " + e.getMessage();
        }
    }
}
