package com.interviewcoach.controller;

import com.interviewcoach.dto.DashboardResponse;
import com.interviewcoach.security.CustomUserDetails;
import com.interviewcoach.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DashboardController - Exposes REST endpoints to supply unified metrics for student performance analytics.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Fetch Placement Analytics Summary Stats
     * GET http://localhost:8080/api/dashboard/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            DashboardResponse response = dashboardService.getStats(userDetails.getUser());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error compiling dashboard metrics: " + ex.getMessage());
        }
    }
}
