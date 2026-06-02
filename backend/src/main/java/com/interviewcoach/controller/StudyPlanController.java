package com.interviewcoach.controller;

import com.interviewcoach.entity.StudyPlan;
import com.interviewcoach.security.CustomUserDetails;
import com.interviewcoach.service.StudyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * StudyPlanController - REST Controller to trigger AI-generated custom placement study schedules.
 */
@RestController
@RequestMapping("/api/studyplan")
public class StudyPlanController {

    @Autowired
    private StudyPlanService studyPlanService;

    /**
     * Generate personalized study plan roadmap
     * POST http://localhost:8080/api/studyplan/generate?durationDays=7
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generatePlan(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestParam("durationDays") int durationDays) {
        if (durationDays != 7 && durationDays != 30) {
            return ResponseEntity.badRequest().body("Roadmap duration must be exactly 7 or 30 days.");
        }
        try {
            StudyPlan plan = studyPlanService.generatePlan(userDetails.getUser(), durationDays);
            return ResponseEntity.ok(plan);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error generating study plan: " + ex.getMessage());
        }
    }

    /**
     * Retrieve Study Plans History Endpoint
     * GET http://localhost:8080/api/studyplan/history
     */
    @GetMapping("/history")
    public ResponseEntity<?> getPlansHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<StudyPlan> history = studyPlanService.getPlans(userDetails.getUser());
            return ResponseEntity.ok(history);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error retrieving study plans: " + ex.getMessage());
        }
    }
}
