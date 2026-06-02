package com.interviewcoach.controller;

import com.interviewcoach.dto.JobHunterResponse;
import com.interviewcoach.security.CustomUserDetails;
import com.interviewcoach.service.JobAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JobAgentController - Handles HTTP requests for the AI Job Hunter agent matches.
 * Fully secure, using @AuthenticationPrincipal for user resolution.
 * Completely free of Lombok annotations to ensure full compilation compliance in STS/Eclipse.
 */
@RestController
@RequestMapping("/api/agent")
public class JobAgentController {

    @Autowired
    private JobAgentService jobAgentService;

    /**
     * Match User CV and return ATS jobs, cover letters, and studying directions.
     * GET http://localhost:8080/api/agent/jobs
     */
    @GetMapping("/jobs")
    public ResponseEntity<?> getTailoredJobs(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            JobHunterResponse response = jobAgentService.getTailoredJobs(userDetails.getUser());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error retrieving tailored jobs: " + ex.getMessage());
        }
    }
}
