package com.espe.ms_healtAnalyzer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class MedicalAlert {
    @Id
    private String alertId;

    private String deviceId;
    private String type;
    private Double value;
    private Double threshold;
    private LocalDateTime timestamp;
}
