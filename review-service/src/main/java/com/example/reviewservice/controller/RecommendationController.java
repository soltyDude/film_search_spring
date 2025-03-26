package com.example.reviewservice.controller;

import com.example.reviewservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Map<String, String>>> getRecommendations(@PathVariable Long userId) {
        List<Map<String, String>> recommendations = recommendationService.getRecommendations(userId.intValue());
        return ResponseEntity.ok(recommendations);
    }
}
