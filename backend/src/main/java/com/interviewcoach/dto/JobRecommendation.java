package com.interviewcoach.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * DTO representing a single job recommendation matched by the AI recruiter agent.
 * Completely free of Lombok annotations to ensure full compilation compliance in STS/Eclipse.
 */
public class JobRecommendation {
    private String jobTitle;
    private String company;
    private String location;
    private int matchScore;
    private List<String> missingKeywords;
    private String tailoredCoverLetter;
    private List<String> prepTopics;

    // Default constructor required for Jackson JSON deserialization
    public JobRecommendation() {
        this.missingKeywords = new ArrayList<>();
        this.prepTopics = new ArrayList<>();
    }

    // Parameterized constructor
    public JobRecommendation(String jobTitle, String company, String location, int matchScore,
                             List<String> missingKeywords, String tailoredCoverLetter, List<String> prepTopics) {
        this.jobTitle = jobTitle;
        this.company = company;
        this.location = location;
        this.matchScore = matchScore;
        this.missingKeywords = missingKeywords != null ? missingKeywords : new ArrayList<>();
        this.tailoredCoverLetter = tailoredCoverLetter;
        this.prepTopics = prepTopics != null ? prepTopics : new ArrayList<>();
    }

    // Getters and Setters
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(List<String> missingKeywords) {
        this.missingKeywords = missingKeywords;
    }

    public String getTailoredCoverLetter() {
        return tailoredCoverLetter;
    }

    public void setTailoredCoverLetter(String tailoredCoverLetter) {
        this.tailoredCoverLetter = tailoredCoverLetter;
    }

    public List<String> getPrepTopics() {
        return prepTopics;
    }

    public void setPrepTopics(List<String> prepTopics) {
        this.prepTopics = prepTopics;
    }
}
