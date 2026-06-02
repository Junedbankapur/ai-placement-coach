package com.interviewcoach.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mock_interviews")
public class MockInterview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Integer score;

    @Lob
    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String feedback;

    @Lob
    @Column(name = "qa_records", columnDefinition = "TEXT")
    private String qaRecords;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MockInterview() {}

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public String getQaRecords() { return qaRecords; }
    public void setQaRecords(String qaRecords) { this.qaRecords = qaRecords; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
