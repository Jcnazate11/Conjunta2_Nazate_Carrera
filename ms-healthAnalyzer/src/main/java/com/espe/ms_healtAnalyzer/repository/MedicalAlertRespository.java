package com.espe.ms_healtAnalyzer.repository;

import com.espe.ms_healtAnalyzer.entity.MedicalAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalAlertRespository extends JpaRepository<MedicalAlert, Long> {
}