package com.espe.ms_healtAnalyzer.repository;

import com.espe.ms_healtAnalyzer.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    List<VitalSign> findByDeviceIdAndTimestampAfter(String deviceId, LocalDateTime timestamp);

    @Query("SELECT DISTINCT v.deviceId FROM VitalSign v")
    List<String> findAllDeviceIds();

    @Query("SELECT v FROM VitalSign v WHERE v.deviceId = :deviceId AND v.timestamp >= :startTime AND v.timestamp <= :endTime")
    List<VitalSign> findByDeviceIdAndTimestampBetween(String deviceId, LocalDateTime startTime, LocalDateTime endTime);
}