package com.interviewcoach.repository;

import com.interviewcoach.entity.MockInterview;
import com.interviewcoach.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * MockInterviewRepository Interface - Handles CRUD for mock interview records.
 */
@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {

    // Retrieves all mock interview history for a user, sorted newest first
    List<MockInterview> findByUserOrderByCreatedAtDesc(User user);
}
