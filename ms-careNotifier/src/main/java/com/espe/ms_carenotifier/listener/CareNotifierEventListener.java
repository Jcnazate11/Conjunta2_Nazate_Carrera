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

    // Escuchar eventos de RabbitMQ
    @RabbitListener(queues = "${rabbitmq.queue.alerts}")  // Asumiendo que la cola se llama "alerts"
    public void onAlertReceived(AlertDTO alertDTO) {
        try {
            log.info("Recibiendo evento de alerta para el dispositivo: {}", alertDTO.getDeviceId());

            // Enviar la notificación a través del servicio
            careNotifierService.sendNotification(alertDTO);

        } catch (Exception e) {
            log.error("Error procesando la alerta: {}", e.getMessage());
            // Aquí se podría almacenar el error o intentar reintentar según la estrategia de resiliencia
        }
    }
}
