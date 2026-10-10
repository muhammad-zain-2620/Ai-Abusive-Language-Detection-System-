package com.detectiondystem.language_detector.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextRequest {
    
    @NotBlank(message = "Text cannot be empty")
    private String text;
    
    private String ipAddress;
    
    private String userAgent;
}