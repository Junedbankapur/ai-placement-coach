package com.interviewcoach.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String skills;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String projects;

    @Lob
    @Column(name = "ai_strengths", columnDefinition = "TEXT")
    private String strengths;

    @Lob
    @Column(name = "ai_weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Resume() {}

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
