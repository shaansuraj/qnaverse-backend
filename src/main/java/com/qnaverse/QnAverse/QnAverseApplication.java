package com.qnaverse.QnAverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the QnAverse Spring Boot application.
 */
@SpringBootApplication
public class QnAverseApplication {

    public static void main(String[] args) {
        SpringApplication.run(QnAverseApplication.class, args);
        System.out.println("🚀 QnAverse Backend is Running...");
    }
}
