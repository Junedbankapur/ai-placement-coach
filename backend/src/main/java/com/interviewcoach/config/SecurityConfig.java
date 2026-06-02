package com.interviewcoach.config;

import com.interviewcoach.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;

/**
 * SecurityConfig - Configure HTTP security, CORS, CSRF, and Bean injections.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - @Configuration & @EnableWebSecurity: Tells Spring Boot this is a security configuration file and registers security filters.
 * - BCryptPasswordEncoder: Standard password hashing algorithm. Rather than saving plain-text passwords (major security hazard), 
 *   it hashes passwords with salt, making it extremely secure against brute force.
 * - Stateless Session: We use SessionCreationPolicy.STATELESS. This disables standard cookies/sessions, letting JWT handle security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password Encoder Bean - Cryptographically hashes user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager Bean - Manages the authentication process in Spring Boot.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Defines the Security Filter Chain (Security rules for REST endpoints)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Enable CORS and Disable CSRF (since we use stateless JWT, CSRF is not required)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. Set Session Management to STATELESS (no cookie storage)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Configure endpoint permissions
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to access the login/register paths
                .requestMatchers("/api/auth/**").permitAll()
                // Require authentication for any other endpoint (protected routes)
                .anyRequest().authenticated()
            );

        // 4. Inject our custom JWT Filter before the standard UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration - Allows our React Frontend (running on port 3000) 
     * to communicate with our Spring Boot Backend (running on port 8080).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
