package com.example.rabbitmq_vital_sign_processor.config;

import com.example.rabbitmq_vital_sign_processor.dto.NewVitalSignEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.new-vital-sign}")
    public void receiveVitalSignEvent(NewVitalSignEventDTO event) {
        System.out.println("Processed vital sign event: " + event);
        // Aquí puedes agregar lógica, como guardar en otra base de datos o notificar
    }
}