package com.espe.ms_healtAnalyzer.dto;

import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportEvent {
    private String reportId;
    private String deviceId;
    private String type;
    private double average;
    private double max;
    private double min;
    private LocalDateTime timestamp;
}
