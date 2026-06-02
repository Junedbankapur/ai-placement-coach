package com.interviewcoach.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.dto.QuestionResponse;
import com.interviewcoach.entity.Question;
import com.interviewcoach.repository.QuestionRepository;
import com.interviewcoach.util.PromptTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private GeminiService geminiService;
    @Autowired
    private ObjectMapper objectMapper;

    public List<QuestionResponse> generateQuestions(String category, String difficulty) {
        String prompt = PromptTemplates.getQuestionGenerationPrompt(category, difficulty);
        String aiJson = geminiService.generateContent(prompt);

        List<QuestionResponse> responseList = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(aiJson);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String qText = node.path("questionText").asText();
                    String diff = node.path("difficulty").asText();
                    String sAns = node.path("sampleAnswer").asText();

                    Question question = new Question();
                    question.setCategory(category);
                    question.setQuestionText(qText);
                    question.setDifficulty(diff);
                    question.setSampleAnswer(sAns);
                    question = questionRepository.save(question);

                    QuestionResponse qr = new QuestionResponse();
                    qr.setId(question.getId());
                    qr.setQuestionText(qText);
                    qr.setDifficulty(diff);
                    qr.setSampleAnswer(sAns);
                    responseList.add(qr);
                }
            }
        } catch (Exception ex) {
            Question question = new Question();
            question.setCategory(category);
            question.setQuestionText("Explain the lifecycle of a Spring Bean in Spring Boot.");
            question.setDifficulty(difficulty);
            question.setSampleAnswer("Spring beans are managed by Spring IoC container.");
            question = questionRepository.save(question);
            
            QuestionResponse qr = new QuestionResponse();
            qr.setId(question.getId());
            qr.setQuestionText(question.getQuestionText());
            qr.setDifficulty(question.getDifficulty());
            qr.setSampleAnswer(question.getSampleAnswer());
            responseList.add(qr);
        }
        return responseList;
    }
}
