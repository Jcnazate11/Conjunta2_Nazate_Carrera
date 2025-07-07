package com.espe.ms_healtAnalyzer.dto;

import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewVitalSignEvent {
    private String eventId;
    private String deviceId;
    private String type;
    private double value;
    private LocalDateTime timestamp;
}
