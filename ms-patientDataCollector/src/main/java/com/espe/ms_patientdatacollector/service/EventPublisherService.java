package com.espe.ms_patientdatacollector.service;



import com.espe.ms_patientdatacollector.dto.NewVitalSignEventDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

    private final AmqpTemplate amqpTemplate;
    @Value("${rabbitmq.exchange.medical-events}") private String exchange;
    @Value("${rabbitmq.routing-key.vital-sign}") private String routingKey;

    public EventPublisherService(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void publishNewVitalSignEvent(NewVitalSignEventDTO eventDTO) {
        amqpTemplate.convertAndSend(exchange, routingKey, eventDTO);
        System.out.println("Evento enviado: " + eventDTO);
    }
}