package com.interviewcoach.controller;

import com.interviewcoach.dto.ResumeResponse;
import com.interviewcoach.security.CustomUserDetails;
import com.interviewcoach.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * ResumeController - RestController managing resume uploads and analyses.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - @AuthenticationPrincipal: Directly injects the authenticated CustomUserDetails from Spring's SecurityContext. 
 *   This is standard practice for fetching the currently logged-in user profile without extra DB queries.
 * - MultipartFile: Standard Spring interface for handling file uploads (PDF/Text).
 */
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * Upload and Analyze Resume Endpoint
     * POST http://localhost:8080/api/resume/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid file.");
        }
        try {
            ResumeResponse response = resumeService.uploadAndAnalyze(userDetails.getUser(), file);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error parsing resume: " + ex.getMessage());
        }
    }

    /**
     * Retrieve Latest Analyzed Resume Endpoint
     * GET http://localhost:8080/api/resume/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestResume(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            ResumeResponse response = resumeService.getLatestResume(userDetails.getUser());
            if (response == null) {
                return ResponseEntity.ok().body("No resume uploaded yet.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error fetching resume: " + ex.getMessage());
        }
    }
}
