package com.espe.ms_patientdatacollector.service;

import com.espe.ms_patientdatacollector.dto.NewVitalSignEventDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

    @Autowired
    private AmqpTemplate amqpTemplate;  // RabbitMQ template para enviar mensajes

    private final String exchange = "vitalSignExchange";  // Nombre del exchange de RabbitMQ
    private final String routingKey = "vitalSignRoutingKey";  // Clave de enrutamiento para el mensaje

    // Publicar el evento NewVitalSignEvent a RabbitMQ
    public void publishNewVitalSignEvent(NewVitalSignEventDTO eventDTO) {
        amqpTemplate.convertAndSend(exchange, routingKey, eventDTO);
        System.out.println("Evento enviado: " + eventDTO);
    }
}
