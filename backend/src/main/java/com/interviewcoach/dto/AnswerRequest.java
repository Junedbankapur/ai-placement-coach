package com.interviewcoach.dto;

public class AnswerRequest {
    private String questionText;
    private String sampleAnswer;
    private String userAnswer;

    public AnswerRequest() {}
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getSampleAnswer() { return sampleAnswer; }
    public void setSampleAnswer(String sampleAnswer) { this.sampleAnswer = sampleAnswer; }
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
}
