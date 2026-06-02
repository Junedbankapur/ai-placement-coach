package com.interviewcoach.dto;

import java.util.List;
import java.util.Map;

public class DashboardResponse {
    private Long totalQuestionsPracticed;
    private Double averageScore;
    private Integer skillsCount;
    private List<String> weakAreas;
    private List<Map<String, Object>> interviewHistory;

    public DashboardResponse() {}
    public Long getTotalQuestionsPracticed() { return totalQuestionsPracticed; }
    public void setTotalQuestionsPracticed(Long totalQuestionsPracticed) { this.totalQuestionsPracticed = totalQuestionsPracticed; }
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
    public Integer getSkillsCount() { return skillsCount; }
    public void setSkillsCount(Integer skillsCount) { this.skillsCount = skillsCount; }
    public List<String> getWeakAreas() { return weakAreas; }
    public void setWeakAreas(List<String> weakAreas) { this.weakAreas = weakAreas; }
    public List<Map<String, Object>> getInterviewHistory() { return interviewHistory; }
    public void setInterviewHistory(List<Map<String, Object>> interviewHistory) { this.interviewHistory = interviewHistory; }
}
