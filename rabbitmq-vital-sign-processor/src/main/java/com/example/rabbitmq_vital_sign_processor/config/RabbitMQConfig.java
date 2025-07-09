package com.example.rabbitmq_vital_sign_processor.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.new-vital-sign}")
    private String queueName;

    @Value("${rabbitmq.exchange.medical-events}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.vital-sign}")
    private String routingKey;

    @Bean
    public DirectExchange medicalEventsExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue vitalSignQueue() {
        return new Queue(queueName, true); // Durable=true
    }

    @Bean
    public Binding vitalSignBinding(Queue vitalSignQueue, DirectExchange medicalEventsExchange) {
        return BindingBuilder.bind(vitalSignQueue).to(medicalEventsExchange).with(routingKey);
    }
}
