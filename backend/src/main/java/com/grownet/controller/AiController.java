
package com.grownet.controller;

import com.grownet.ai.AiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> askCareerQuestion(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", "Please provide a career question."
                    )
            );
        }

        String answer = aiService.getCareerAdvice(question);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "answer", answer
                )
        );
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> aiHealth() {
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "status", "available",
                        "service", "GrowNet AI Career Assistant"
                )
        );
    }
}

