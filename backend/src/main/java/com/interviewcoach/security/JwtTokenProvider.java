package com.interviewcoach.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * JwtTokenProvider - Handles JWT generation, parsing, and validation.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - JWT (JSON Web Token): A secure mechanism for sharing claims between a client and server.
 * - Why JWT? In a standard session-based app, the server stores session IDs in memory. In a stateless microservice or SPA architecture, 
 *   using JWT means the backend doesn't store active sessions. Instead, the token contains encrypted, self-signed validation parameters, 
 *   making the backend perfectly stateless.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationInMs;

    // Helper to generate the signature verification key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generates a secure JWT token on successful login
     */
    public String generateToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Signs the token securely using HMAC SHA-256
                .compact();
    }

    /**
     * Extracts username claim from JWT token payload
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Validates a JWT token's signature and expiration state
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // In a production app, you would log these exceptions (ExpiredJwtException, MalformedJwtException, etc.)
            System.err.println("JWT Validation Error: " + ex.getMessage());
        }
        return false;
    }
}
