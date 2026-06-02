package com.interviewcoach.service;

import com.interviewcoach.entity.Resume;
import com.interviewcoach.entity.StudyPlan;
import com.interviewcoach.entity.User;
import com.interviewcoach.repository.ResumeRepository;
import com.interviewcoach.repository.StudyPlanRepository;
import com.interviewcoach.util.PromptTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudyPlanService {
    @Autowired
    private StudyPlanRepository studyPlanRepository;
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private GeminiService geminiService;

    public StudyPlan generatePlan(User user, int durationDays) {
        List<Resume> resumes = resumeRepository.findByUserOrderByCreatedAtDesc(user);
        String skills = "Java, SQL";
        String weaknesses = "Mock practice, security integrations.";

        if (!resumes.isEmpty()) {
            Resume resume = resumes.get(0);
            if (resume.getSkills() != null) skills = resume.getSkills();
            if (resume.getWeaknesses() != null) weaknesses = resume.getWeaknesses();
        }

        String prompt = PromptTemplates.getStudyPlanPrompt(skills, weaknesses, durationDays);
        String aiJson = geminiService.generateContent(prompt);

        StudyPlan plan = new StudyPlan();
        plan.setUser(user);
        plan.setDurationDays(durationDays);
        plan.setTitle(durationDays + "-Day Placement Strategy");
        plan.setRoadmapJson(aiJson);
        return studyPlanRepository.save(plan);
    }

    public List<StudyPlan> getPlans(User user) {
        return studyPlanRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
