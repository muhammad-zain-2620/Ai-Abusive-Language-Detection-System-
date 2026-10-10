package com.detectiondystem.language_detector.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "detection_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String inputText;
    
    @Column(nullable = false)
    private Boolean isAbusive;
    
    @Column(nullable = false)
    private String severityLevel;
    
    @Column(nullable = false)
    private Integer abuseScore;
    
    @Column(columnDefinition = "TEXT")
    private String flaggedWords;
    
    @Column(length = 45)
    private String ipAddress;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column
    private Integer aiConfidence;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime detectedAt;
}