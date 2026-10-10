package com.detectiondystem.language_detector.controller;

import com.detectiondystem.language_detector.dto.DetectionResponse;
import com.detectiondystem.language_detector.dto.TextRequest;
import com.detectiondystem.language_detector.entity.DetectionLog;
import com.detectiondystem.language_detector.service.EnhancedAbuseDetectionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detect")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class DetectionController {
    
    private final EnhancedAbuseDetectionService detectionService;
    
    @PostMapping("/analyze")
    public ResponseEntity<DetectionResponse> analyzeText(
            @Valid @RequestBody TextRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            request.setIpAddress(getClientIpAddress(httpRequest));
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            
            log.info("Received analysis request from IP: {}", request.getIpAddress());
            
            DetectionResponse response = detectionService.analyzeText(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error analyzing text: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DetectionResponse.builder()
                            .message("Error processing request: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/logs")
    public ResponseEntity<List<DetectionLog>> getAllLogs() {
        try {
            return ResponseEntity.ok(detectionService.getAllLogs());
        } catch (Exception e) {
            log.error("Error fetching logs: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/logs/abusive")
    public ResponseEntity<List<DetectionLog>> getAbusiveLogs() {
        try {
            return ResponseEntity.ok(detectionService.getAbusiveLogs());
        } catch (Exception e) {
            log.error("Error fetching abusive logs: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDetections", detectionService.getAllLogs().size());
            stats.put("abusiveCount", detectionService.getAbusiveCount());
            stats.put("safeCount", detectionService.getAllLogs().size() - detectionService.getAbusiveCount());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "AI-Powered Abusive Language Detection");
        health.put("version", "2.0.0");
        return ResponseEntity.ok(health);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}