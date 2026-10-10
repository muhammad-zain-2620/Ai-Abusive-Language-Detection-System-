package com.detectiondystem.language_detector.repository;

import com.detectiondystem.language_detector.entity.AbusiveWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbusiveWordRepository extends JpaRepository<AbusiveWord, Long> {
    
    Optional<AbusiveWord> findByWord(String word);
    List<AbusiveWord> findBySeverity(String severity);
    List<AbusiveWord> findByCategory(String category);
    boolean existsByWord(String word);
}