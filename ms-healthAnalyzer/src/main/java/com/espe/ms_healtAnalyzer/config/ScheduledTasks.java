package com.espe.ms_healtAnalyzer.config;

import com.espe.ms_healtAnalyzer.dto.AlertEvent;
import com.espe.ms_healtAnalyzer.dto.DailyReportEvent;
import com.espe.ms_healtAnalyzer.entity.VitalSign;
import com.espe.ms_healtAnalyzer.repository.VitalSignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0 0 0 * * ?") // Cada 24 horas a medianoche
    public void generateDailyReport() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(1);

        List<String> deviceIds = vitalSignRepository.findAllDeviceIds();
        for (String deviceId : deviceIds) {
            for (String type : List.of("heart-rate", "oxygen", "blood-pressure-systolic", "blood-pressure-diastolic")) {
                List<VitalSign> vitalSigns = vitalSignRepository.findByDeviceIdAndTypeAndTimestampBetween(
                        deviceId, type, startTime, endTime);

                if (!vitalSigns.isEmpty()) {
                    double average = vitalSigns.stream().mapToDouble(VitalSign::getValue).average().orElse(0.0);
                    double max = vitalSigns.stream().mapToDouble(VitalSign::getValue).max().orElse(0.0);
                    double min = vitalSigns.stream().mapToDouble(VitalSign::getValue).min().orElse(0.0);

                    DailyReportEvent report = new DailyReportEvent(
                            "RPT-" + UUID.randomUUID(), deviceId, type, average, max, min, endTime
                    );
                    rabbitTemplate.convertAndSend("reports-exchange", "reports.daily", report);
                    logger.info("Reporte diario generado: {}", report);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 */6 * * ?") // Cada 6 horas
    public void checkInactiveDevices() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<String> deviceIds = vitalSignRepository.findAllDeviceIds();

        for (String deviceId : deviceIds) {
            List<VitalSign> recentData = vitalSignRepository.findByDeviceIdAndTimestampAfter(deviceId, threshold);
            if (recentData.isEmpty()) {
                AlertEvent alert = new AlertEvent(
                        "ALT-" + UUID.randomUUID(),
                        "DeviceOfflineAlert",
                        deviceId,
                        0.0,
                        0.0,
                        LocalDateTime.now()
                );
                rabbitTemplate.convertAndSend("alerts-exchange", "alerts.critical", alert);
                logger.warn("Dispositivo inactivo detectado: {}", deviceId);
            }
        }
    }
}
