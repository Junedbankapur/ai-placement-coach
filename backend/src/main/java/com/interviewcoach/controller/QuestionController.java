package com.interviewcoach.controller;

import com.interviewcoach.dto.QuestionResponse;
import com.interviewcoach.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * QuestionController - REST endpoint for generating customized technical and HR placement questions.
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * Generate Placement Questions
     * GET http://localhost:8080/api/questions/generate?category=Java&difficulty=INTERMEDIATE
     */
    @GetMapping("/generate")
    public ResponseEntity<?> generateQuestions(@RequestParam("category") String category,
                                               @RequestParam("difficulty") String difficulty) {
        try {
            List<QuestionResponse> questions = questionService.generateQuestions(category, difficulty);
            return ResponseEntity.ok(questions);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error generating questions: " + ex.getMessage());
        }
    }
}
