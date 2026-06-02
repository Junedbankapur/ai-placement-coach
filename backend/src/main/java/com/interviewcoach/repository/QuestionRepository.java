package com.interviewcoach.repository;

import com.interviewcoach.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * QuestionRepository Interface - Handles CRUD for generated questions.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Retrieves questions for a specific category (e.g., "Java", "SQL")
    List<Question> findByCategory(String category);

    // Retrieves questions filtered by category and difficulty (e.g., "Spring Boot", "BEGINNER")
    List<Question> findByCategoryAndDifficulty(String category, String difficulty);
}
