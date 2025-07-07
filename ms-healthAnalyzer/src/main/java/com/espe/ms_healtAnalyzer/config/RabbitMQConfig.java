package com.espe.ms_healtAnalyzer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Cola para recibir eventos de signos vitales
    @Bean
    public Queue vitalSignsQueue() {
        return QueueBuilder.durable("vital-signs-queue").build();
    }

    // Intercambio para enviar alertas
    @Bean
    public TopicExchange alertsExchange() {
        return new TopicExchange("alerts-exchange");
    }

    // Binding entre la cola de alertas y el intercambio
    @Bean
    public Binding alertsBinding(Queue vitalSignsQueue, TopicExchange alertsExchange) {
        return BindingBuilder
                .bind(vitalSignsQueue)
                .to(alertsExchange)
                .with("alerts.critical");
    }

    // Intercambio para enviar reportes diarios
    @Bean
    public TopicExchange reportsExchange() {
        return new TopicExchange("reports-exchange");
    }

    // Binding entre la cola de reportes y el intercambio (opcional, si usas una cola específica)
    @Bean
    public Binding reportsBinding(Queue vitalSignsQueue, TopicExchange reportsExchange) {
        return BindingBuilder
                .bind(vitalSignsQueue)
                .to(reportsExchange)
                .with("reports.daily");
    }
}


