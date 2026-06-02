package com.interviewcoach.repository;

import com.interviewcoach.entity.Resume;
import com.interviewcoach.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ResumeRepository Interface - Handles CRUD for resumes.
 */
@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // Retrieves all uploaded resumes for a user, sorted newest first
    List<Resume> findByUserOrderByCreatedAtDesc(User user);
}
