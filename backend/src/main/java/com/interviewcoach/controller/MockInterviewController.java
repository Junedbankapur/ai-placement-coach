package com.interviewcoach.controller;

import com.interviewcoach.dto.MockInterviewRequest;
import com.interviewcoach.dto.MockInterviewResponse;
import com.interviewcoach.security.CustomUserDetails;
import com.interviewcoach.service.MockInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * MockInterviewController - RestController for submitting interview sessions and reviewing mock histories.
 */
@RestController
@RequestMapping("/api/mock")
public class MockInterviewController {

    @Autowired
    private MockInterviewService mockInterviewService;

    /**
     * Submit Mock Session for AI Grading & Feedback
     * POST http://localhost:8080/api/mock/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitSession(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestBody MockInterviewRequest request) {
        try {
            MockInterviewResponse response = mockInterviewService.evaluateMockSession(userDetails.getUser(), request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error grading mock interview: " + ex.getMessage());
        }
    }

    /**
     * Fetch Mock History Endpoint
     * GET http://localhost:8080/api/mock/history
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<MockInterviewResponse> history = mockInterviewService.getHistory(userDetails.getUser());
            return ResponseEntity.ok(history);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error fetching history: " + ex.getMessage());
        }
    }
}
