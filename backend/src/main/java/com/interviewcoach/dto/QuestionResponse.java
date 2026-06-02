package com.interviewcoach.dto;

public class QuestionResponse {
    private Long id;
    private String questionText;
    private String difficulty;
    private String sampleAnswer;

    public QuestionResponse() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getSampleAnswer() { return sampleAnswer; }
    public void setSampleAnswer(String sampleAnswer) { this.sampleAnswer = sampleAnswer; }
}
