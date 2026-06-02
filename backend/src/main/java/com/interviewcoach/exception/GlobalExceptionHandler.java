package com.interviewcoach.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Intercepts all REST controller errors application-wide.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - @ControllerAdvice: Enables intercepting exceptions thrown by handler methods in any controller.
 * - @ExceptionHandler: Defines which specific exceptions this method catches (e.g. RuntimeException, Exception).
 * - Benefit: Prevents raw server stack traces from leaking to the frontend. It returns a clean, uniform JSON error layout 
 *   with timestamps and message fields, which is the standard protocol for enterprise APIs.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catches any custom RuntimeExceptions thrown in our Service layers
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fallback to intercept any unhandled System Errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected system error occurred: " + ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
