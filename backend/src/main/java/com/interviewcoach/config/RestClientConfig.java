package com.interviewcoach.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * RestClientConfig - Configures HTTP Client capabilities for Spring Boot 3.2.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - RestClient: Introduced in Spring Boot 3.2, it is a synchronous HTTP client that offers 
 *   a modern, fluent API. It serves as the modern successor to RestTemplate and WebClient for synchronous requests, 
 *   greatly simplifying how we integrate external APIs (like Google Gemini) without bulky third-party SDKs.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        // Builds a standard, pre-configured RestClient instance
        return RestClient.builder().build();
    }
}
