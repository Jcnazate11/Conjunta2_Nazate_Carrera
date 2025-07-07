package com.espe.ms_healtAnalyzer.dto;

import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertEvent {
    private String alertId;
    private String type;
    private String deviceId;
    private double value;
    private double threshold;
    private LocalDateTime timestamp;
}
