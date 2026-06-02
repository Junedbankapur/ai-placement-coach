package com.interviewcoach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point of the Spring Boot Backend Application.
 * 
 * EXPLAINING THIS FOR INTERVIEWS:
 * - @SpringBootApplication: This is a convenience annotation that combines three key annotations:
 *   1. @EnableAutoConfiguration: Tells Spring Boot to automatically configure beans based on dependencies in pom.xml.
 *   2. @ComponentScan: Directs Spring to scan the current package (com.interviewcoach) and its sub-packages 
 *      to find classes annotated with @Controller, @Service, @Repository, or @Component and register them in the Application Context.
 *   3. @Configuration: Allows registering extra beans or importing other config classes.
 */
@SpringBootApplication
public class AiInterviewCoachApplication {

    public static void main(String[] args) {
        // Starts the entire Spring Boot framework, initializes the embedded Tomcat web server (port 8080), 
        // and establishes the application context.
        SpringApplication.run(AiInterviewCoachApplication.class, args);
    }
}
