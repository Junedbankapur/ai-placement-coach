package com.interviewcoach.controller;

import com.interviewcoach.dto.AuthRequest;
import com.interviewcoach.dto.AuthResponse;
import com.interviewcoach.dto.RegisterRequest;
import com.interviewcoach.entity.User;
import com.interviewcoach.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - RestController exposing registration and authentication endpoints.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - @RestController: Marks this as a REST controller where all handler methods return JSON directly.
 * - @RequestMapping("/api/auth"): Sets the base URL path for this controller's endpoints.
 * - ResponseEntity: Standard wrapper for HTTP responses, allowing us to specify HTTP status codes (like 200 OK or 400 Bad Request) 
 *   and custom response bodies.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User Registration Endpoint
     * POST http://localhost:8080/api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User registeredUser = authService.registerUser(request);
            return ResponseEntity.ok("User registered successfully with ID: " + registeredUser.getId());
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * User Login Endpoint
     * POST http://localhost:8080/api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.authenticateUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Invalid username or password! Details: " + ex.getMessage());
        }
    }
}
