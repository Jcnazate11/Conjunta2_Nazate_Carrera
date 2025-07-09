package com.espe.ms_carenotifier.config;

import com.espe.ms_carenotifier.dto.AlertDTO;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.DefaultClassMapper;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.alerts}")
    private String alertsQueue; // -> ej. "alerts-critical-queue"

    @Value("${rabbitmq.exchange.medical-events}")
    private String exchangeName; // -> "alerts-exchange"

    @Value("${rabbitmq.routing-key.alert}")
    private String routingKey; // -> "alerts.#"

    @Bean
    public Queue alertQueue() {
        return new Queue(alertsQueue, true);  // Cola durable (persistente)
    }

    @Bean
    public TopicExchange medicalEventsExchange() {
        return new TopicExchange(exchangeName);  // Exchange tipo Topic
    }

    @Bean
    public Binding alertQueueBinding() {
        return BindingBuilder.bind(alertQueue())
                .to(medicalEventsExchange())
                .with(routingKey); // Enruta todos los mensajes que comiencen con 'alerts.'
    }
    @Bean
    public TopicExchange alertsExchange() {
        return new TopicExchange("alerts-exchange");
    }

    @Bean
    public Queue heartRateQueue() {
        return new Queue("heart-rate-alert-queue", true);
    }

    @Bean
    public Queue oxygenQueue() {
        return new Queue("oxygen-alert-queue", true);
    }

    @Bean
    public Queue pressureQueue() {
        return new Queue("pressure-alert-queue", true);
    }

    @Bean
    public Queue deviceOfflineQueue() {
        return new Queue("device-offline-alert-queue", true);
    }

    @Bean
    public Binding bindHeartRate(TopicExchange alertsExchange) {
        return BindingBuilder.bind(heartRateQueue()).to(alertsExchange).with("alerts.heart-rate");
    }

    @Bean
    public Binding bindOxygen(TopicExchange alertsExchange) {
        return BindingBuilder.bind(oxygenQueue()).to(alertsExchange).with("alerts.oxygen");
    }

    @Bean
    public Binding bindPressure(TopicExchange alertsExchange) {
        return BindingBuilder.bind(pressureQueue()).to(alertsExchange).with("alerts.pressure");
    }

    @Bean
    public Binding bindDeviceOffline(TopicExchange alertsExchange) {
        return BindingBuilder.bind(deviceOfflineQueue()).to(alertsExchange).with("alerts.device-offline");
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();

        // Mapea el nombre del tipo recibido a tu DTO local
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.espe.ms_healtAnalyzer.dto.AlertEvent", AlertDTO.class); // 👈 esto es clave

        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setTrustedPackages("*"); // Puedes restringir si lo deseas
        converter.setClassMapper(classMapper);
        return converter;
    }
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder
                .simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }


}
