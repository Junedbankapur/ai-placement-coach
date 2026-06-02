package com.interviewcoach.util;

/**
 * PromptTemplates - A central repository for highly optimized AI prompts.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - Prompt Engineering: The science of structuring instructions so that LLMs return predictable outputs.
 * - JSON Enforcement (JSON Mode): We explicitly command Gemini to return raw JSON data matching a specific schema, 
 *   disallowing conversational filler (like "Here is your JSON:"). This allows our backend to parse the text directly 
 *   into Java DTO objects using Jackson ObjectMapper.
 */
public class PromptTemplates {

    /**
     * Prompt for Resume Analysis
     */
    public static String getResumeAnalysisPrompt(String resumeText) {
        return "You are an expert technical recruiter and resume auditor.\n" +
               "Analyze the following resume text and extract the details in a strict JSON format.\n" +
               "Do NOT write any conversational intro or outro. Respond ONLY with a valid JSON object matching this schema:\n" +
               "{\n" +
               "  \"skills\": [\"list\", \"of\", \"extracted\", \"skills\"],\n" +
               "  \"projects\": [\"Extracted Project Name 1: Short summary of what was done\", \"Project 2: Summary\"],\n" +
               "  \"strengths\": [\"Bullet point strength 1\", \"Bullet point strength 2\"],\n" +
               "  \"weaknesses\": [\"Constructive area of improvement 1\", \"Constructive area of improvement 2\"]\n" +
               "}\n\n" +
               "Resume text to analyze:\n" +
               resumeText;
    }

    /**
     * Prompt for Question Generation
     */
    public static String getQuestionGenerationPrompt(String category, String difficulty) {
        return "You are an elite software engineering interviewer.\n" +
               "Generate exactly 5 interview questions for the category \"" + category + "\" and difficulty level \"" + difficulty + "\".\n" +
               "Provide a technical sample answer for each question.\n" +
               "Do NOT write any conversational intro or outro. Respond ONLY with a valid JSON array of objects matching this schema:\n" +
               "[\n" +
               "  {\n" +
               "    \"questionText\": \"What is X?\",\n" +
               "    \"difficulty\": \"" + difficulty + "\",\n" +
               "    \"sampleAnswer\": \"A technical explanation of X including core concepts.\"\n" +
               "  }\n" +
               "]\n";
    }

    /**
     * Prompt for Mock Interview Answer Evaluation
     */
    public static String getAnswerEvaluationPrompt(String question, String sampleAnswer, String userAnswer) {
        return "You are a senior technical interviewer conducting a mock coding interview.\n" +
               "Evaluate the candidate's answer based on the question and the ideal reference answer.\n" +
               "Grade the answer strictly but constructively, assigning a score from 0 to 100.\n" +
               "Do NOT write any conversational intro or outro. Respond ONLY with a valid JSON object matching this schema:\n" +
               "{\n" +
               "  \"score\": 85,\n" +
               "  \"feedback\": \"Granular feedback bullet points detailing: 1. What was correct. 2. What details were missed or incorrect. 3. How to improve the explanation in the real interview.\"\n" +
               "}\n\n" +
               "Interview Details:\n" +
               "- Question: " + question + "\n" +
               "- Reference Ideal Answer: " + sampleAnswer + "\n" +
               "- Candidate's Answer: " + userAnswer + "\n";
    }

    /**
     * Prompt for Study Plan (Roadmap) Generation
     */
    public static String getStudyPlanPrompt(String skills, String weaknesses, int durationDays) {
        return "You are a professional career coach specializing in software engineering placements.\n" +
               "Generate a personalized " + durationDays + "-day study plan to help a candidate patch their weaknesses.\n" +
               "Target weaknesses: " + weaknesses + "\n" +
               "Current skills: " + skills + "\n" +
               "Do NOT write any conversational intro or outro. Respond ONLY with a valid JSON object matching this schema:\n" +
               "{\n" +
               "  \"title\": \"Personalized " + durationDays + "-Day Placement Strategy\",\n" +
               "  \"days\": [\n" +
               "    {\n" +
               "      \"dayNumber\": 1,\n" +
               "      \"topic\": \"Core Concept to study\",\n" +
               "      \"tasks\": [\"Task 1: Read about X\", \"Task 2: Build a micro-project for Y\"],\n" +
               "      \"estimatedHours\": 4\n" +
               "    }\n" +
               "  ]\n" +
               "}\n";
    }

    /**
     * Prompt for Job Hunter and ATS matching.
     */
    public static String getJobHunterPrompt(String skills, String projects) {
        return "You are an expert technical recruiter, career agent, and ATS optimization specialist.\n" +
               "Generate exactly 3 professional job openings tailored to the candidate's technical profile.\n" +
               "Candidate skills: " + skills + "\n" +
               "Candidate projects: " + projects + "\n" +
               "For each job opening, you must map the technical alignment and calculate a Match Score (from 0 to 100).\n" +
               "Identify 3-5 critical missing ATS keywords/skills that are highly relevant to this specific role but missing from the candidate's CV.\n" +
               "Draft a personalized cover letter (150-200 words) that bridges the skills gap and highlights the candidate's projects for this role.\n" +
               "Recommend 3 specific technical topics the candidate should practice to crack this company's interview.\n" +
               "Do NOT write any conversational intro or outro. Respond ONLY with a valid JSON array of 3 objects matching this schema:\n" +
               "[\n" +
               "  {\n" +
               "    \"jobTitle\": \"Job Title (e.g., Associate Software Engineer)\",\n" +
               "    \"company\": \"Company Name (e.g., Oracle)\",\n" +
               "    \"location\": \"Location (e.g., Bengaluru, India / Remote)\",\n" +
               "    \"matchScore\": 85,\n" +
               "    \"missingKeywords\": [\"Keyword1\", \"Keyword2\"],\n" +
               "    \"tailoredCoverLetter\": \"Dear Hiring Manager,...\",\n" +
               "    \"prepTopics\": [\"Topic1\", \"Topic2\"]\n" +
               "  }\n" +
               "]\n";
    }
}
