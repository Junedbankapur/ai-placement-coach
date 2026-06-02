package com.interviewcoach.service;

import com.interviewcoach.dto.DashboardResponse;
import com.interviewcoach.entity.MockInterview;
import com.interviewcoach.entity.Resume;
import com.interviewcoach.entity.User;
import com.interviewcoach.repository.MockInterviewRepository;
import com.interviewcoach.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {
    @Autowired
    private MockInterviewRepository mockInterviewRepository;
    @Autowired
    private ResumeRepository resumeRepository;

    public DashboardResponse getStats(User user) {
        List<MockInterview> interviews = mockInterviewRepository.findByUserOrderByCreatedAtDesc(user);
        long totalQuestions = 0;
        double scoreSum = 0;
        List<Map<String, Object>> history = new ArrayList<>();

        for (MockInterview item : interviews) {
            scoreSum += item.getScore();
            if (item.getQaRecords() != null) {
                totalQuestions += item.getQaRecords().split("\n").length;
            }
            Map<String, Object> point = new HashMap<>();
            point.put("date", item.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd")));
            point.put("score", item.getScore());
            point.put("category", item.getCategory());
            history.add(point);
        }

        double averageScore = interviews.isEmpty() ? 0.0 : Math.round((scoreSum / interviews.size()) * 10.0) / 10.0;
        Collections.reverse(history);

        List<Resume> resumes = resumeRepository.findByUserOrderByCreatedAtDesc(user);
        int skillsCount = 0;
        List<String> weakAreas = new ArrayList<>();

        if (!resumes.isEmpty()) {
            Resume resume = resumes.get(0);
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                skillsCount = resume.getSkills().split(",").length;
            }
            if (resume.getWeaknesses() != null && !resume.getWeaknesses().isEmpty()) {
                weakAreas.addAll(Arrays.asList(resume.getWeaknesses().split("\n")));
            }
        } else {
            weakAreas.add("Upload a resume to analyze weakness areas");
        }

        DashboardResponse response = new DashboardResponse();
        response.setTotalQuestionsPracticed(totalQuestions);
        response.setAverageScore(averageScore);
        response.setSkillsCount(skillsCount);
        response.setWeakAreas(weakAreas);
        response.setInterviewHistory(history);
        return response;
    }
}
