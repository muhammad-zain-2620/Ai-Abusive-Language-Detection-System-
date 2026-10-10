package com.detectiondystem.language_detector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LanguageDetectorApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LanguageDetectorApplication.class, args);
        System.out.println("========================================");
        System.out.println("🛡️  AI-Powered Language Detector Started!");
        System.out.println("🤖 Powered by Groq AI");
        System.out.println("📍 API: http://localhost:8080/api/detect");
        System.out.println("🏥 Health: http://localhost:8080/api/detect/health");
        System.out.println("========================================");
    }
}