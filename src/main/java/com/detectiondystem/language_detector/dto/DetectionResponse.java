package com.detectiondystem.language_detector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectionResponse {
    
    private Boolean isAbusive;
    private String severityLevel;
    private Integer abuseScore;
    private Integer confidence;
    private List<FlaggedWord> flaggedWords;
    private Integer totalWords;
    private Integer characterCount;
    private String message;
    private Map<String, Object> aiAnalysis;
    private List<String> detectedPatterns;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FlaggedWord {
        private String word;
        private String severity;
        private String category;
    }
}