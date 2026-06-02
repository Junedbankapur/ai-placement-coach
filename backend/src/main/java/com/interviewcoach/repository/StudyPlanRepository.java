package com.interviewcoach.repository;

import com.interviewcoach.entity.StudyPlan;
import com.interviewcoach.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * StudyPlanRepository Interface - Handles CRUD for study plans.
 */
@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    // Retrieves all generated study plans for a user, sorted newest first
    List<StudyPlan> findByUserOrderByCreatedAtDesc(User user);
}
