package com.interviewcoach.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String category;

    @Lob
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Lob
    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer;

    public Question() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getSampleAnswer() { return sampleAnswer; }
    public void setSampleAnswer(String sampleAnswer) { this.sampleAnswer = sampleAnswer; }
}
