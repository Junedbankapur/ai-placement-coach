package com.interviewcoach.repository;

import com.interviewcoach.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * UserRepository Interface
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - @Repository: Tells Spring Boot this is a database access component.
 * - JpaRepository<User, Long>: Extends Spring Data JPA, providing full CRUD (Create, Read, Update, Delete) 
 *   capabilities out-of-the-box without writing a single line of SQL!
 * - Custom Query Methods: Spring dynamically creates queries from method names (e.g. "findByUsername" automatically 
 *   translates to: SELECT * FROM users WHERE username = ?).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during Login and JWT validation
    Optional<User> findByUsername(String username);

    // Used during Registration to check duplicate accounts
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
