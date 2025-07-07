package com.espe.ms_carenotifier.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.alerts}")
    private String alertsQueue;

    @Value("${rabbitmq.exchange.medical-events}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.alert}")
    private String routingKey;

    @Bean
    public Queue alertQueue() {
        return new Queue(alertsQueue, true);  // La cola es durable (los mensajes persisten)
    }

    @Bean
    public TopicExchange medicalEventsExchange() {
        return new TopicExchange(exchangeName);  // Tipo de exchange: Topic
    }

    @Bean
    public Binding alertQueueBinding() {
        return BindingBuilder.bind(alertQueue())  // Vincula la cola
                .to(medicalEventsExchange())  // Al exchange
                .with(routingKey);  // Clave de enrutamiento para los eventos de alerta
    }
}
