package com.example.rabbitmq_vital_sign_processor.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewVitalSignEventDTO {
    private String eventId;
    private String deviceId;
    private String type;
    private Double value;
    private LocalDateTime timestamp;
}