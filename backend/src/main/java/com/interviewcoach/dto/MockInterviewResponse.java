package com.interviewcoach.dto;

public class MockInterviewResponse {
    private Long id;
    private String category;
    private Integer score;
    private String feedback;
    private String qaRecords;

    public MockInterviewResponse() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public String getQaRecords() { return qaRecords; }
    public void setQaRecords(String qaRecords) { this.qaRecords = qaRecords; }
}
