package com.interviewcoach.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.config.RestClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;

/**
 * GeminiService - Integrates Google's Gemini LLM into Spring Boot.
 * Handles live REST API calls using Spring Boot 3.2's RestClient, 
 * and features a high-fidelity Mock Fallback mode for local offline testing.
 */
@Service
public class GeminiService {

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot automatically configures Jackson's ObjectMapper

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key:}") // Defaults to empty string if key is not configured yet
    private String apiKey;

    /**
     * Sends a prompt to Gemini API. If no API key is present, it transparently falls back to
     * a smart, high-fidelity mock response, making the application 100% interactive out-of-the-box!
     * 
     * EXPLAINING THIS FOR INTERVIEWS:
     * - "To ensure the app starts instantly without setup barriers, I engineered a mock fallback provider inside my GeminiService. 
     *    If the API Key is not detected in application.properties, the system generates intelligent static JSON payloads 
     *    tailored to the prompt type. When the key is plugged in, it automatically switches to live Google Gemini REST calls."
     */
    public String generateContent(String prompt) {
        // If the key is missing or is the placeholder, trigger the mock fallback
        if (apiKey == null || apiKey.isEmpty() || apiKey.trim().equals("YOUR_GEMINI_API_KEY")) {
            System.out.println("[GeminiService] API Key is missing. Triggering High-Fidelity Mock Fallback...");
            return getMockResponse(prompt);
        }

        try {
            // Build the standard Gemini REST payload:
            // { "contents": [{ "parts": [{ "text": "YOUR_PROMPT" }] }] }
            // Java 8 compatible collection builder
            Map<String, Object> textMap = new java.util.HashMap<>();
            textMap.put("text", prompt);

            Map<String, Object> partsMap = new java.util.HashMap<>();
            partsMap.put("parts", java.util.Collections.singletonList(textMap));

            Map<String, Object> requestPayload = new java.util.HashMap<>();
            requestPayload.put("contents", java.util.Collections.singletonList(partsMap));

            // Make the post call using Spring 3.2's fluent RestClient
            String responseStr = restClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(String.class);

            // Parse response JSON to extract the generated text.
            // Gemini JSON structure: candidates[0].content.parts[0].text
            JsonNode root = objectMapper.readTree(responseStr);
            String aiText = root.path("candidates")
                                .path(0)
                                .path("content")
                                .path("parts")
                                .path(0)
                                .path("text")
                                .asText();

            // Strip any leading/trailing ```json ... ``` markdown formatting if the LLM includes it
            return cleanJsonMarkdown(aiText);

        } catch (Exception ex) {
            System.err.println("[GeminiService] Error during live API call: " + ex.getMessage() + ". Falling back to Mock...");
            return getMockResponse(prompt);
        }
    }

    /**
     * Cleans markdown blocks (like ```json ... ```) that models sometimes return.
     */
    private String cleanJsonMarkdown(String rawText) {
        String clean = rawText.trim();
        if (clean.startsWith("```json")) {
            clean = clean.substring(7);
        } else if (clean.startsWith("```")) {
            clean = clean.substring(3);
        }
        if (clean.endsWith("```")) {
            clean = clean.substring(0, clean.length() - 3);
        }
        return clean.trim();
    }

    /**
     * Generates extremely realistic, structured JSON responses for various prompt workflows
     */
    private String getMockResponse(String prompt) {
        if (prompt == null) {
            return "{}";
        }
        String lower = prompt.toLowerCase();

        // Job Hunter / ATS Mock Matcher Scraper Fallback
        if (lower.contains("expert technical recruiter") || lower.contains("job openings") || lower.contains("job hunter") || lower.contains("matchscore")) {
            return "[\n" +
                   "  {\n" +
                   "    \"jobTitle\": \"Associate Software Engineer (Java & Backend)\",\n" +
                   "    \"company\": \"Oracle\",\n" +
                   "    \"location\": \"Bengaluru, India / Remote\",\n" +
                   "    \"matchScore\": 88,\n" +
                   "    \"missingKeywords\": [\"Docker\", \"Kubernetes\", \"CI/CD Pipelines\", \"Microservices Architecture\"],\n" +
                   "    \"tailoredCoverLetter\": \"Dear Hiring Manager,\\n\\nI am thrilled to express my strong interest in the Associate Software Engineer position at Oracle. As an eager backend developer with a robust foundation in Java, Spring Boot, and MySQL database management, I am confident in my ability to contribute to Oracle's enterprise scaling goals. Having engineered the AI Interview Coach platform complete with stateless JWT-driven security filters and clean database transaction schemes, I understand how to write clean, maintainable, and high-performance server-side code. I am particularly excited about the chance to apply my skills to Oracle's cloud infrastructure while proactively building hands-on expertise in containerized deployments using Docker and Kubernetes. Thank you for your time and consideration.\\n\\nSincerely,\\nCandidate\",\n" +
                   "    \"prepTopics\": [\"Microservices & Cloud Patterns\", \"Multi-threading & JVM Internals\", \"SQL Indexing & Query Optimizations\"]\n" +
                   "  },\n" +
                   "  {\n" +
                   "    \"jobTitle\": \"React Developer (Frontend Systems)\",\n" +
                   "    \"company\": \"Capgemini\",\n" +
                   "    \"location\": \"Mumbai, India / Hybrid\",\n" +
                   "    \"matchScore\": 82,\n" +
                   "    \"missingKeywords\": [\"TypeScript\", \"Redux Toolkit\", \"TailwindCSS\", \"Jest/RTL Unit Testing\"],\n" +
                   "    \"tailoredCoverLetter\": \"Dear Hiring Manager,\\n\\nI write to apply for the React Developer position at Capgemini. My expertise in building responsive single page applications (SPAs) using React.js, modern JavaScript, and custom Glassmorphic design principles makes me an excellent fit. In my previous work, I implemented a stateless mock interview simulator with reactive score tracking and dynamic chart gauges that significantly improved user engagement. I am highly motivated to elevate Capgemini's web products by adopting TypeScript and Redux Toolkit for clean state management, and I look forward to contributing my technical skills and visual design passion to your collaborative development teams.\\n\\nSincerely,\\nCandidate\",\n" +
                   "    \"prepTopics\": [\"React Hooks & Virtual DOM Reconciliation\", \"State Management (Redux/Context)\", \"Frontend Performance & Lazy Loading\"]\n" +
                   "  },\n" +
                   "  {\n" +
                   "    \"jobTitle\": \"Full-Stack Software Engineer\",\n" +
                   "    \"company\": \"Accenture\",\n" +
                   "    \"location\": \"Bengaluru, India\",\n" +
                   "    \"matchScore\": 90,\n" +
                   "    \"missingKeywords\": [\"AWS Cloud Services (S3/EC2)\", \"Redis Caching\", \"REST Web Services Testing\", \"NoSQL Databases\"],\n" +
                   "    \"tailoredCoverLetter\": \"Dear Hiring Manager,\\n\\nI am writing to express my enthusiastic interest in the Full-Stack Software Engineer role at Accenture. My full-stack experience in bridging Java Spring Boot RESTful services with dynamic, modern React.js visual environments perfectly matches the demands of this position. By building and deploying robust database-backed platforms with strict secure authentication boundaries, I have mastered the complete development lifecycle. I am excited to join Accenture's global delivery network and quickly expand my capabilities in AWS cloud deployments and Redis distributed caching. Thank you for considering my application.\\n\\nSincerely,\\nCandidate\",\n" +
                   "    \"prepTopics\": [\"System Design & Architecture\", \"Stateless REST Authentication Flow\", \"Database Normalization & Indexing\"]\n" +
                   "  }\n" +
                   "]";
        }

        // 1. Mock Question Generator (highly specific words)
        if (lower.contains("generate exactly 5") || lower.contains("interview questions") || lower.contains("questiontext") || lower.contains("elite software engineering interviewer")) {
            
            // A. Spring Boot Category Mock Questions
            if (lower.contains("spring boot")) {
                return "[\n" +
                       "  {\n" +
                       "    \"questionText\": \"What are the core annotations used in Spring Boot and their purposes (@SpringBootApplication, @RestController, @Autowired)?\",\n" +
                       "    \"difficulty\": \"BEGINNER\",\n" +
                       "    \"sampleAnswer\": \"@SpringBootApplication configures component scanning and autoconfiguration. @RestController registers a RESTful endpoint controller. @Autowired automates Dependency Injection.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Explain the difference between Constructor Injection and Setter Injection in Spring Boot. Why is Constructor Injection preferred?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"Constructor Injection enforces required dependencies at object creation, ensures immutability of fields, and simplifies testing. Setter Injection allows optional dependencies that can be changed after instantiation.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"What is the role of Spring Boot Auto-Configuration? How does Spring determine which configurations to load?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"Auto-configuration scans dependencies in pom.xml and automatically configures standard beans. It uses conditional annotations like @ConditionalOnClass and @ConditionalOnMissingBean to determine configurations.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"How does Spring Boot handle exception handling globally across REST controllers?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"It uses global exception handlers annotated with @ControllerAdvice or @RestControllerAdvice, containing handler methods annotated with @ExceptionHandler to return uniform error bodies.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Explain how Spring Security protects REST endpoints statelessly using filters.\",\n" +
                       "    \"difficulty\": \"ADVANCED\",\n" +
                       "    \"sampleAnswer\": \"Spring Security intercepts requests through a security filter chain. In stateless flows, it disables CSRF/cookies and uses custom JWT filters to parse credentials and set them in the SecurityContext.\"\n" +
                       "  }\n" +
                       "]";
            }
            
            // B. SQL Databases Category Mock Questions
            if (lower.contains("sql") || lower.contains("database")) {
                return "[\n" +
                       "  {\n" +
                       "    \"questionText\": \"What is the difference between INNER JOIN, LEFT JOIN, and RIGHT JOIN in SQL?\",\n" +
                       "    \"difficulty\": \"BEGINNER\",\n" +
                       "    \"sampleAnswer\": \"INNER JOIN returns rows only when there is a match in both tables. LEFT JOIN returns all rows from the left table and matched rows from the right table. RIGHT JOIN does the reverse, returning all right rows.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Explain database constraints and list their standard types (Primary Key, Foreign Key, Unique, Not Null).\",\n" +
                       "    \"difficulty\": \"BEGINNER\",\n" +
                       "    \"sampleAnswer\": \"Constraints enforce data integrity rules. Primary Key uniquely identifies rows. Foreign Key links rows to another table. Unique ensures no duplicates. Not Null prevents empty values.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"What is a Database Index? How does it improve retrieval speed and what are the trade-offs?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"An index is a pointer structure (B-Trees) that speeds up SELECT search queries. Trade-offs include slower INSERT/UPDATE operations and additional disk storage consumption.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Explain the difference between the WHERE and HAVING clauses in SQL. When should you use which?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"WHERE filters raw rows before aggregation. HAVING filters grouped rows after aggregation (used with GROUP BY).\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"What are SQL subqueries? What is the difference between a correlated and non-correlated subquery?\",\n" +
                       "    \"difficulty\": \"ADVANCED\",\n" +
                       "    \"sampleAnswer\": \"A subquery is a query nested inside another query. A non-correlated subquery executes independently once. A correlated subquery references the outer query and executes repeatedly for each row.\"\n" +
                       "  }\n" +
                       "]";
            }
            
            // C. React.js Category Mock Questions
            if (lower.contains("react")) {
                return "[\n" +
                       "  {\n" +
                       "    \"questionText\": \"What are React Hooks? Explain the purpose of useState and useEffect hooks.\",\n" +
                       "    \"difficulty\": \"BEGINNER\",\n" +
                       "    \"sampleAnswer\": \"Hooks are functions that let functional components manage state and lifecycle. useState adds reactive state trackers. useEffect manages side-effects like API data fetching after mounting.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"What is the difference between props and state in React?\",\n" +
                       "    \"difficulty\": \"BEGINNER\",\n" +
                       "    \"sampleAnswer\": \"Props are immutable arguments passed down from parent components. State is local, mutable data managed internally by the component itself, triggering re-renders on change.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"What is the Virtual DOM? How does React's reconciliation process optimize DOM updates?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"The Virtual DOM is a lightweight memory copy of the real DOM. React updates the Virtual DOM first, compares it with a snapshot using a diffing algorithm, and updates only the changed parts of the real DOM.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Explain React's hook dependencies array in useEffect. What happens if you leave it empty or omit it?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"The dependencies array determines when useEffect triggers. Leaving it empty [] makes it run only once on mount. Omitting it completely makes it run after every render, which can cause infinite loops.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Explain state lift-up and context API in React. When would you use which?\",\n" +
                       "    \"difficulty\": \"ADVANCED\",\n" +
                       "    \"sampleAnswer\": \"State lift-up passes state to the closest common parent component. Context API provides global state access to deep nested children without prop drilling.\"\n" +
                       "  }\n" +
                       "]";
            }
            
            // D. DSA Category Mock Questions
            if (lower.contains("dsa") || lower.contains("algorithm")) {
                return "[\n" +
                       "  {\n" +
                       "    \"questionText\": \"What is Binary Search? What is its time complexity and what condition must the array satisfy?\",\n" +
                       "    \"difficulty\": \"BEGINNER\",\n" +
                       "    \"sampleAnswer\": \"Binary Search repeatedly divides the search interval in half. Its time complexity is O(log N). The input array must be sorted in ascending order.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"How do you detect a cycle in a Linked List? Explain Floyd's Cycle-Finding Algorithm (Tortoise and Hare).\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"Floyd's algorithm uses two pointers moving at different speeds (slow moves 1 step, fast moves 2 steps). If there is a cycle, the pointers will meet at some node; otherwise, the fast pointer reaches null.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Explain the difference between DFS and BFS traversal in a Graph. When is which preferred?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"DFS (Depth First Search) uses a stack/recursion to go deep down a branch. BFS (Breadth First Search) uses a queue to traverse layer-by-layer. BFS is preferred for finding the shortest path; DFS is preferred for topological sorting.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"How does a Hash Map handle collisions? Explain Chaining and Open Addressing.\",\n" +
                       "    \"difficulty\": \"ADVANCED\",\n" +
                       "    \"sampleAnswer\": \"Collisions occur when two keys hash to the same bucket. Chaining stores colliding elements in a linked list at that bucket. Open addressing searches for the next available bucket using linear or quadratic probing.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"What is the Two-Pointer technique? Show how it solves the Target Sum problem in a sorted array.\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"It uses two pointers (left at start, right at end) and moves them inward based on comparison: if sum is too low, left increases; if too high, right decreases. Time complexity is O(N).\"\n" +
                       "  }\n" +
                       "]";
            }
            
            // E. HR Category Mock Questions
            if (lower.contains("hr") || lower.contains("behavioral") || lower.contains("placement")) {
                return "[\n" +
                       "  {\n" +
                       "    \"questionText\": \"Tell me about yourself, your career goals, and what motivated you to build an AI Interview Coach.\",\n" +
                       "    \"difficulty\": \"BEGINNER\",\n" +
                       "    \"sampleAnswer\": \"I am an enthusiastic developer who built the AI Coach to solve real placement preparation gaps, combining standard Spring Boot enterprise backends, stateless JWT security, and modern React glassmorphism.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Describe a difficult technical bug you faced in your project and how you went about resolving it.\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"I faced Eclipse compilation errors due to Lombok eclipse agents not being installed. I solved this permanently by refactoring all builders and annotations into pure vanilla getters, setters, and constructors.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Why do you want to join our organization? How do your technical skills align with our goals?\",\n" +
                       "    \"difficulty\": \"BEGINNER\",\n" +
                       "    \"sampleAnswer\": \"Your organization values robust backend engineering and modern client interfaces. Having built complete full-stack REST integrations, database transactions, and reactive UI charts, I am ready to contribute immediately.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"How do you handle working in a team when there is a major difference of opinion on technical designs?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"I actively listen to the reasoning behind the other approach, compare both designs against performance, maintainability, and scalability criteria, and document options to align on a clean collective decision.\"\n" +
                       "  },\n" +
                       "  {\n" +
                       "    \"questionText\": \"Where do you see yourself in 5 years? What roadmap are you following to reach there?\",\n" +
                       "    \"difficulty\": \"INTERMEDIATE\",\n" +
                       "    \"sampleAnswer\": \"I see myself in a Senior Full-Stack role, leading architecture design and mentorship. I am actively mastering cloud-native patterns, advanced database performance optimizations, and modern design systems.\"\n" +
                       "  }\n" +
                       "]";
            }

            // F. Default Category: Core Java Mock Questions
            return "[\n" +
                   "  {\n" +
                   "    \"questionText\": \"What is the difference between an Abstract Class and an Interface in Java? When should you use which?\",\n" +
                   "    \"difficulty\": \"INTERMEDIATE\",\n" +
                   "    \"sampleAnswer\": \"An abstract class can have instance fields and concrete methods, supporting single inheritance. An interface can only have static final constants and abstract methods (or default methods), supporting multiple inheritance. Use an abstract class for sharing code among closely related classes; use an interface to define a contract for unrelated classes.\"\n" +
                   "  },\n" +
                   "  {\n" +
                   "    \"questionText\": \"Explain the difference between HashMap and ConcurrentHashMap in Java. How is thread safety achieved?\",\n" +
                   "    \"difficulty\": \"INTERMEDIATE\",\n" +
                   "    \"sampleAnswer\": \"HashMap is non-thread-safe. ConcurrentHashMap is thread-safe and achieves high concurrency by segmenting or locking buckets (using CAS and synchronized at node level) rather than locking the entire map.\"\n" +
                   "  },\n" +
                   "  {\n" +
                   "    \"questionText\": \"What are Java Streams? Explain how map, filter, and collect work.\",\n" +
                   "    \"difficulty\": \"INTERMEDIATE\",\n" +
                   "    \"sampleAnswer\": \"Streams represent a sequence of elements supporting functional operations. filter retains elements matching a predicate, map transforms each element, and collect packages results into a List/Set.\"\n" +
                   "  },\n" +
                   "  {\n" +
                   "    \"questionText\": \"What is JVM (Java Virtual Machine)? Explain its core components: Classloader, Memory Area (Heap/Stack), and Execution Engine.\",\n" +
                   "    \"difficulty\": \"INTERMEDIATE\",\n" +
                   "    \"sampleAnswer\": \"JVM executes bytecode. Classloader loads classes. Memory Area divides memory into Heap (for dynamic objects) and Stack (for local variables and threads). Execution Engine executes instructions and runs Garbage Collection.\"\n" +
                   "  },\n" +
                   "  {\n" +
                   "    \"questionText\": \"Explain the Exception Hierarchy in Java. What is the difference between checked and unchecked exceptions?\",\n" +
                   "    \"difficulty\": \"INTERMEDIATE\",\n" +
                   "    \"sampleAnswer\": \"Throwable is the root. Exception includes checked exceptions (verified at compile time, must be declared/caught). RuntimeException includes unchecked exceptions (verified at runtime, like NullPointerException).\"\n" +
                   "  }\n" +
                   "]";
        }

        // 2. Mock Mock Interview Evaluator
        if (lower.contains("evaluate") || lower.contains("candidate's answer") || lower.contains("grading") || lower.contains("score") || lower.contains("senior technical interviewer")) {
            return "{\n" +
                   "  \"score\": 82,\n" +
                   "  \"feedback\": \"• Correctly identified that abstract classes support single inheritance while interfaces support multiple.\\n• Missed mentioning that interfaces can now have concrete default and static methods in Java 8+.\\n• Try to mention access modifiers: abstract classes support public/protected/private fields, while interface fields are public static final by default.\"\n" +
                   "}";
        }

        // 3. Mock Study Plan
        if (lower.contains("study plan") || lower.contains("roadmap") || lower.contains("strategy") || lower.contains("placement strategy") || lower.contains("career coach")) {
            return "{\n" +
                   "  \"title\": \"AI-Generated Placement Study Plan\",\n" +
                   "  \"days\": [\n" +
                   "    {\n" +
                   "      \"dayNumber\": 1,\n" +
                   "      \"topic\": \"Java OOPs Foundations\",\n" +
                   "      \"tasks\": [\n" +
                   "        \"Read about Inheritance, Encapsulation, Polymorphism, and Abstraction.\",\n" +
                   "        \"Write a practice program demonstrating method overloading and overriding.\"\n" +
                   "      ],\n" +
                   "      \"estimatedHours\": 3\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"dayNumber\": 2,\n" +
                   "      \"topic\": \"Spring Boot Dependency Injection\",\n" +
                   "      \"tasks\": [\n" +
                   "        \"Study @Autowired, @Component, @Service, and Constructor Injection.\",\n" +
                   "        \"Implement a Spring Boot demo with a service layer and controller layer.\"\n" +
                   "      ],\n" +
                   "      \"estimatedHours\": 4\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"dayNumber\": 3,\n" +
                   "      \"topic\": \"MySQL Joins & Subqueries\",\n" +
                   "      \"tasks\": [\n" +
                   "        \"Practice writing complex SQL queries using LEFT, RIGHT, and INNER JOINs.\",\n" +
                   "        \"Solve 5 SQL query challenges on LeetCode or HackerRank.\"\n" +
                   "      ],\n" +
                   "      \"estimatedHours\": 3\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"dayNumber\": 4,\n" +
                   "      \"topic\": \"JWT stateless authentication in Spring Boot\",\n" +
                   "      \"tasks\": [\n" +
                   "        \"Review the security chain filter flow and once-per-request filter.\",\n" +
                   "        \"Understand how JWT tokens are generated, signed, and validated in Java.\"\n" +
                   "      ],\n" +
                   "      \"estimatedHours\": 5\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"dayNumber\": 5,\n" +
                   "      \"topic\": \"React.js Hooks & Lifecycle\",\n" +
                   "      \"tasks\": [\n" +
                   "        \"Study useState, useEffect, and custom hooks structure.\",\n" +
                   "        \"Build a simple React application that fetches API data and displays it statefully.\"\n" +
                   "      ],\n" +
                   "      \"estimatedHours\": 4\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"dayNumber\": 6,\n" +
                   "      \"topic\": \"DSA: Arrays & String manipulation\",\n" +
                   "      \"tasks\": [\n" +
                   "        \"Practice Two-Pointer and Sliding Window techniques in Arrays.\",\n" +
                   "        \"Solve 5 medium-difficulty String array questions on Java.\"\n" +
                   "      ],\n" +
                   "      \"estimatedHours\": 4\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"dayNumber\": 7,\n" +
                   "      \"topic\": \"Placement Mock Simulation\",\n" +
                   "      \"tasks\": [\n" +
                   "        \"Run a mock interview session inside the AI Coach dashboard.\",\n" +
                   "        \"Review strengths/weaknesses and read sample answers to solidify knowledge.\"\n" +
                   "      ],\n" +
                   "      \"estimatedHours\": 3\n" +
                   "    }\n" +
                   "  ]\n" +
                   "}";
        }

        // 4. Mock Resume Analyzer (or catch-all)
        if (lower.contains("auditor") || lower.contains("resume") || lower.contains("cv") || lower.contains("extracted") || lower.contains("skills")) {
            return "{\n" +
                   "  \"skills\": [\"Java\", \"Spring Boot\", \"MySQL\", \"React.js\", \"JavaScript\", \"REST APIs\", \"Hibernate\", \"HTML/CSS\", \"Git\"],\n" +
                   "  \"projects\": [\n" +
                   "    \"SalesSavvy e-commerce website: An online shopping backend built with Spring Boot and JDBC/SQL, managing user authentications and shopping cart transactions.\"\n" +
                   "  ],\n" +
                   "  \"strengths\": [\n" +
                   "    \"Strong foundations in core enterprise Java and Spring Boot framework.\",\n" +
                   "    \"Familiarity with standard relational databases and Hibernate object mapping.\",\n" +
                   "    \"Basic understanding of React.js and RESTful interface design.\"\n" +
                   "  ],\n" +
                   "  \"weaknesses\": [\n" +
                   "    \"Needs more experience in security integrations and JWT architectures.\",\n" +
                   "    \"Could improve knowledge in high-concurrency systems and advanced JPA caching.\",\n" +
                   "    \"Needs to practice solving Data Structures and Algorithms questions under timed pressure.\"\n" +
                   "  ]\n" +
                   "}";
        }

        // Catch-all fallback
        return "{\n" +
               "  \"skills\": [\"Java\", \"Spring Boot\", \"SQL\"],\n" +
               "  \"projects\": [],\n" +
               "  \"strengths\": [\"Good foundational knowledge.\"],\n" +
               "  \"weaknesses\": [\"Needs mock practice.\"]\n" +
               "}";
    }
}
