package com.interviewcoach.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * Parent DTO response for the AI Job Hunter scanner.
 * Completely free of Lombok annotations to ensure full compilation compliance in STS/Eclipse.
 */
public class JobHunterResponse {
    private List<String> auditedSkills;
    private List<JobRecommendation> recommendations;

    // Default constructor
    public JobHunterResponse() {
        this.auditedSkills = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    // Parameterized constructor
    public JobHunterResponse(List<String> auditedSkills, List<JobRecommendation> recommendations) {
        this.auditedSkills = auditedSkills != null ? auditedSkills : new ArrayList<>();
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
    }

    // Getters and Setters
    public List<String> getAuditedSkills() {
        return auditedSkills;
    }

    public void setAuditedSkills(List<String> auditedSkills) {
        this.auditedSkills = auditedSkills;
    }

    public List<JobRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<JobRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
