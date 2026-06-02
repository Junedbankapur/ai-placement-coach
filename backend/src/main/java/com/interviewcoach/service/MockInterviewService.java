package com.interviewcoach.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.dto.AnswerRequest;
import com.interviewcoach.dto.MockInterviewRequest;
import com.interviewcoach.dto.MockInterviewResponse;
import com.interviewcoach.entity.MockInterview;
import com.interviewcoach.entity.User;
import com.interviewcoach.repository.MockInterviewRepository;
import com.interviewcoach.util.PromptTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MockInterviewService {
    @Autowired
    private MockInterviewRepository mockInterviewRepository;
    @Autowired
    private GeminiService geminiService;
    @Autowired
    private ObjectMapper objectMapper;

    public MockInterviewResponse evaluateMockSession(User user, MockInterviewRequest request) {
        StringBuilder qaBuilder = new StringBuilder();
        List<String> records = new ArrayList<>();
        
        for (int i = 0; i < request.getAnswers().size(); i++) {
            AnswerRequest ans = request.getAnswers().get(i);
            qaBuilder.append("Q: ").append(ans.getQuestionText()).append("\nA: ").append(ans.getUserAnswer()).append("\n\n");
            records.add(String.format("Q: %s | A: %s", ans.getQuestionText(), ans.getUserAnswer()));
        }

        String prompt = PromptTemplates.getAnswerEvaluationPrompt("Mock Interview", "See QAs below", qaBuilder.toString());
        String aiJson = geminiService.generateContent(prompt);

        int score = 75;
        String feedback = "Evaluation completed.";

        try {
            JsonNode root = objectMapper.readTree(aiJson);
            if (root.has("score")) score = root.get("score").asInt();
            if (root.has("feedback")) feedback = root.get("feedback").asText();
        } catch (Exception ex) {}

        MockInterview interview = new MockInterview();
        interview.setUser(user);
        interview.setCategory(request.getCategory());
        interview.setScore(score);
        interview.setFeedback(feedback);
        interview.setQaRecords(String.join("\n", records));
        interview = mockInterviewRepository.save(interview);

        MockInterviewResponse response = new MockInterviewResponse();
        response.setId(interview.getId());
        response.setCategory(interview.getCategory());
        response.setScore(interview.getScore());
        response.setFeedback(interview.getFeedback());
        response.setQaRecords(interview.getQaRecords());
        return response;
    }

    public List<MockInterviewResponse> getHistory(User user) {
        List<MockInterview> list = mockInterviewRepository.findByUserOrderByCreatedAtDesc(user);
        List<MockInterviewResponse> responseList = new ArrayList<>();
        for (MockInterview item : list) {
            MockInterviewResponse r = new MockInterviewResponse();
            r.setId(item.getId());
            r.setCategory(item.getCategory());
            r.setScore(item.getScore());
            r.setFeedback(item.getFeedback());
            r.setQaRecords(item.getQaRecords());
            responseList.add(r);
        }
        return responseList;
    }
}
