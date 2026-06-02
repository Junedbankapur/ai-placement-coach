package com.interviewcoach.dto;

import java.util.List;

public class MockInterviewRequest {
    private String category;
    private List<AnswerRequest> answers;

    public MockInterviewRequest() {}
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<AnswerRequest> getAnswers() { return answers; }
    public void setAnswers(List<AnswerRequest> answers) { this.answers = answers; }
}
