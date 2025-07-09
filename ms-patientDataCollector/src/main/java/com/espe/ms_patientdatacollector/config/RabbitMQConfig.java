package com.espe.ms_patientdatacollector.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.new-vital-sign:vital-sign-queue}")
    private String queueName;

    @Value("${rabbitmq.exchange.medical-events:medical-events-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.vital-sign:vital-sign-routing-key}")
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
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}