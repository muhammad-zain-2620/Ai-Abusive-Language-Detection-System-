package com.detectiondystem.language_detector.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "abusive_words")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbusiveWord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String word;
    
    @Column(nullable = false)
    private String severity;
    
    @Column(nullable = false)
    private String category;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}   