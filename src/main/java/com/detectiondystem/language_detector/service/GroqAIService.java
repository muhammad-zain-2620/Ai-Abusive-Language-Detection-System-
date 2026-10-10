package com.detectiondystem.language_detector.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GroqAIService {
    
    @Value("${groq.api.key:}")
    private String groqApiKey;
    
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Map<String, Object> analyzeWithAI(String text) {
        if (groqApiKey == null || groqApiKey.isEmpty()) {
            log.warn("Groq API key not configured. Skipping AI analysis.");
            return createDefaultResponse();
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", 
                    "You are an expert content moderation AI. Analyze text for abusive, offensive, " +
                    "toxic, hateful, or inappropriate content. Respond ONLY with a valid JSON object " +
                    "containing: {\"is_toxic\": boolean, \"toxicity_score\": number (0-100), " +
                    "\"categories\": [array of strings], \"severity\": string (\"low\"|\"medium\"|\"high\"), " +
                    "\"explanation\": string, \"confidence\": number (0-100)}"),
                Map.of("role", "user", "content", 
                    "Analyze this text for abusive/toxic content: \"" + text + "\"")
            ));
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 500);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                GROQ_API_URL, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            return parseGroqResponse(response.getBody());
            
        } catch (Exception e) {
            log.error("Error calling Groq AI: ", e);
            return createDefaultResponse();
        }
    }
    
    private Map<String, Object> parseGroqResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0)
                .path("message").path("content").asText();
            
            content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            
            JsonNode aiResponse = objectMapper.readTree(content);
            
            Map<String, Object> result = new HashMap<>();
            result.put("is_toxic", aiResponse.path("is_toxic").asBoolean(false));
            result.put("toxicity_score", aiResponse.path("toxicity_score").asInt(0));
            result.put("categories", objectMapper.convertValue(
                aiResponse.path("categories"), List.class));
            result.put("severity", aiResponse.path("severity").asText("low"));
            result.put("explanation", aiResponse.path("explanation").asText(""));
            result.put("confidence", aiResponse.path("confidence").asInt(0));
            result.put("ai_enabled", true);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error parsing Groq response: ", e);
            return createDefaultResponse();
        }
    }
    
    private Map<String, Object> createDefaultResponse() {
        Map<String, Object> result = new HashMap<>();
        result.put("is_toxic", false);
        result.put("toxicity_score", 0);
        result.put("categories", List.of());
        result.put("severity", "low");
        result.put("explanation", "AI analysis not available");
        result.put("confidence", 0);
        result.put("ai_enabled", false);
        return result;
    }
}