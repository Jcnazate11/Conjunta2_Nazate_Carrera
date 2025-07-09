package com.espe.ms_carenotifier.listener;

import com.espe.ms_carenotifier.dto.AlertDTO;
import com.espe.ms_carenotifier.service.CareNotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CareNotifierEventListener {

    private static final Logger log = LoggerFactory.getLogger(CareNotifierEventListener.class);
    private final CareNotifierService careNotifierService;

    public CareNotifierEventListener(CareNotifierService careNotifierService) {
        this.careNotifierService = careNotifierService;
    }

    @RabbitListener(queues = "heart-rate-alert-queue")
    public void listenHeartRate(AlertDTO alert) {
        log.info("💓 Alerta de ritmo cardíaco recibida: {}", alert);
        careNotifierService.sendNotification(alert);
    }

    @RabbitListener(queues = "oxygen-alert-queue")
    public void listenOxygen(AlertDTO alert) {
        log.info("🫁 Alerta de oxígeno recibida: {}", alert);
        careNotifierService.sendNotification(alert);
    }

    @RabbitListener(queues = "pressure-alert-queue")
    public void listenPressure(AlertDTO alert) {
        log.info("🩸 Alerta de presión recibida: {}", alert);
        careNotifierService.sendNotification(alert);
    }

    @RabbitListener(queues = "device-offline-alert-queue")
    public void listenDeviceOffline(AlertDTO alert) {
        log.info("🔌 Alerta de dispositivo desconectado recibida: {}", alert);
        careNotifierService.sendNotification(alert);
    }
}
