package com.espe.ms_healtAnalyzer.repository;

import com.espe.ms_healtAnalyzer.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {

    List<VitalSign> findByDeviceIdAndTimestampAfter(String deviceId, LocalDateTime timestamp);

    @Query("SELECT DISTINCT v.deviceId FROM VitalSign v")
    List<String> findAllDeviceIds();

    @Query("SELECT v FROM VitalSign v WHERE v.deviceId = :deviceId AND v.type = :type AND v.timestamp BETWEEN :startTime AND :endTime")
    List<VitalSign> findByDeviceIdAndTypeAndTimestampBetween(String deviceId, String type, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT DISTINCT v.deviceId FROM VitalSign v WHERE v.deviceId NOT IN (" +
            "SELECT v2.deviceId FROM VitalSign v2 WHERE v2.timestamp > :since)")
    List<String> findInactiveDevicesSince(@Param("since") LocalDateTime since);
}