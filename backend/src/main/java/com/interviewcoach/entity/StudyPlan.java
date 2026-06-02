package com.interviewcoach.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_plans")
public class StudyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(name = "roadmap_json", columnDefinition = "TEXT")
    private String roadmapJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public StudyPlan() {}

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getRoadmapJson() { return roadmapJson; }
    public void setRoadmapJson(String roadmapJson) { this.roadmapJson = roadmapJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
