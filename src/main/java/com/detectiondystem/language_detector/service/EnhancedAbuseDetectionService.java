package com.detectiondystem.language_detector.service;

import com.detectiondystem.language_detector.dto.DetectionResponse;
import com.detectiondystem.language_detector.dto.TextRequest;
import com.detectiondystem.language_detector.entity.DetectionLog;
import com.detectiondystem.language_detector.repository.DetectionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedAbuseDetectionService {
    
    private final GroqAIService groqAIService;
    private final DetectionLogRepository detectionLogRepository;
    
    @Transactional
    public DetectionResponse analyzeText(TextRequest request) {
        log.info("Analyzing text with AI: {}", 
            request.getText().substring(0, Math.min(50, request.getText().length())));
        
        String text = request.getText().trim();
        
        // Primary AI Analysis
        Map<String, Object> aiAnalysis = groqAIService.analyzeWithAI(text);
        
        // Extract AI results
        boolean isToxic = (boolean) aiAnalysis.getOrDefault("is_toxic", false);
        int toxicityScore = (int) aiAnalysis.getOrDefault("toxicity_score", 0);
        String aiSeverity = (String) aiAnalysis.getOrDefault("severity", "low");
        int aiConfidence = (int) aiAnalysis.getOrDefault("confidence", 0);
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) aiAnalysis.getOrDefault("categories", new ArrayList<>());
        
        // Convert AI analysis to our format
        String severityLevel = mapAISeverityToLevel(aiSeverity, toxicityScore);
        int abuseScore = calculateAbuseScore(toxicityScore, categories.size());
        
        // Create flagged words from categories
        List<DetectionResponse.FlaggedWord> flaggedWords = categories.stream()
            .map(cat -> DetectionResponse.FlaggedWord.builder()
                .word(cat)
                .severity(aiSeverity.toUpperCase())
                .category(cat)
                .build())
            .collect(Collectors.toList());
        
        // Determine message
        String message = generateMessage(severityLevel, isToxic);
        
        // Calculate statistics
        String[] words = text.split("\\s+");
        int totalWords = words.length;
        int characterCount = text.length();
        
        // Save to database for learning and tracking
        saveDetectionLog(request, isToxic, severityLevel, abuseScore, 
            categories, aiConfidence);
        
        // Build comprehensive response
        return DetectionResponse.builder()
            .isAbusive(isToxic)
            .severityLevel(severityLevel)
            .abuseScore(abuseScore)
            .confidence(aiConfidence)
            .flaggedWords(flaggedWords)
            .totalWords(totalWords)
            .characterCount(characterCount)
            .message(message)
            .aiAnalysis(aiAnalysis)
            .detectedPatterns(categories)
            .build();
    }
    
    private String mapAISeverityToLevel(String aiSeverity, int toxicityScore) {
        if (toxicityScore >= 70 || "high".equalsIgnoreCase(aiSeverity)) {
            return "DANGER";
        } else if (toxicityScore >= 40 || "medium".equalsIgnoreCase(aiSeverity)) {
            return "WARNING";
        } else {
            return "SAFE";
        }
    }
    
    private int calculateAbuseScore(int toxicityScore, int categoryCount) {
        int baseScore = (int) Math.ceil(toxicityScore * 15.0 / 100.0);
        int categoryBonus = Math.min(categoryCount, 3);
        return Math.min(baseScore + categoryBonus, 15);
    }
    
    private String generateMessage(String severityLevel, boolean isToxic) {
        return switch (severityLevel) {
            case "DANGER" -> "⚠️ High-risk abusive content detected by AI";
            case "WARNING" -> "⚠️ Potentially inappropriate content detected";
            default -> isToxic ? "Content flagged by AI" : "✅ Content appears safe";
        };
    }
    
    private void saveDetectionLog(TextRequest request, Boolean isAbusive, 
                                 String severityLevel, Integer abuseScore,
                                 List<String> categories, Integer aiConfidence) {
        
        String categoriesStr = String.join(", ", categories);
        
        DetectionLog detectionLog = new DetectionLog();
        detectionLog.setInputText(request.getText());
        detectionLog.setIsAbusive(isAbusive);
        detectionLog.setSeverityLevel(severityLevel);
        detectionLog.setAbuseScore(abuseScore);
        detectionLog.setFlaggedWords(categoriesStr);
        detectionLog.setIpAddress(request.getIpAddress());
        detectionLog.setUserAgent(request.getUserAgent());
        detectionLog.setAiConfidence(aiConfidence);
        
        detectionLogRepository.save(detectionLog);
        log.info("AI detection log saved with ID: {}", detectionLog.getId());
    }
    
    public List<DetectionLog> getAllLogs() {
        return detectionLogRepository.findAll();
    }
    
    public List<DetectionLog> getAbusiveLogs() {
        return detectionLogRepository.findByIsAbusive(true);
    }
    
    public Long getAbusiveCount() {
        return detectionLogRepository.countAbusiveDetections();
    }
}