package com.espe.ms_healtAnalyzer.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.new-vital-sign:vital-sign-queue}")
    private String vitalSignQueue;

    @Value("${rabbitmq.exchange.medical-events:medical-events-exchange}")
    private String medicalEventsExchange;

    @Value("${rabbitmq.routing-key.vital-sign:vital-sign-routing-key}")
    private String routingKey;

    @Bean
    public Queue queue() {
        return new Queue(vitalSignQueue, true); // durable
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(medicalEventsExchange);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public TopicExchange alertsExchange() {
        return new TopicExchange("alerts-exchange");
    }

    @Bean
    public Queue heartRateQueue() {
        return new Queue("heart-rate-alert-queue");
    }

    @Bean
    public Queue oxygenQueue() {
        return new Queue("oxygen-alert-queue");
    }

    @Bean
    public Queue pressureQueue() {
        return new Queue("pressure-alert-queue");
    }

    @Bean
    public Queue deviceOfflineQueue() {
        return new Queue("device-offline-alert-queue");
    }

    @Bean
    public Binding heartRateBinding() {
        return BindingBuilder.bind(heartRateQueue()).to(alertsExchange()).with("alerts.heart-rate");
    }

    @Bean
    public Binding oxygenBinding() {
        return BindingBuilder.bind(oxygenQueue()).to(alertsExchange()).with("alerts.oxygen");
    }

    @Bean
    public Binding pressureBinding() {
        return BindingBuilder.bind(pressureQueue()).to(alertsExchange()).with("alerts.pressure");
    }

    @Bean
    public Binding offlineBinding() {
        return BindingBuilder.bind(deviceOfflineQueue()).to(alertsExchange()).with("alerts.device-offline");
    }
}