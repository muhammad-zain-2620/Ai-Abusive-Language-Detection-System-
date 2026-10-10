package com.detectiondystem.language_detector.repository;

import com.detectiondystem.language_detector.entity.DetectionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetectionLogRepository extends JpaRepository<DetectionLog, Long> {
    
    List<DetectionLog> findByIsAbusive(Boolean isAbusive);
    List<DetectionLog> findBySeverityLevel(String severityLevel);
    
    @Query("SELECT d FROM DetectionLog d WHERE d.detectedAt BETWEEN ?1 AND ?2")
    List<DetectionLog> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(d) FROM DetectionLog d WHERE d.isAbusive = true")
    Long countAbusiveDetections();
}