package com.interviewcoach.dto;

import java.util.List;

public class ResumeResponse {
    private Long id;
    private String fileName;
    private List<String> skills;
    private List<String> projects;
    private List<String> strengths;
    private List<String> weaknesses;

    public ResumeResponse() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public List<String> getProjects() { return projects; }
    public void setProjects(List<String> projects) { this.projects = projects; }
    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }
    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }
}
