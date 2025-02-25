package com.qnaverse.QnAverse.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qnaverse.QnAverse.services.ReportService;
import com.qnaverse.QnAverse.utils.JwtUtil;

/**
 * Controller for reporting questions/answers.
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;
    private final JwtUtil jwtUtil;

    public ReportController(ReportService reportService, JwtUtil jwtUtil) {
        this.reportService = reportService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Report a question or answer.
     * @param token Bearer token
     * @param contentId ID of question or answer
     * @param contentType "QUESTION" or "ANSWER"
     * @param reason one of (Sensitive, Mature, Self-harm, Violence, or any reason)
     */
    @PostMapping("/{contentId}/{contentType}")
    public ResponseEntity<?> reportContent(@RequestHeader("Authorization") String token,
                                           @PathVariable Long contentId,
                                           @PathVariable String contentType,
                                           @RequestParam String reason) {
        String username = extractUsername(token);
        return reportService.reportContent(username, contentId, contentType, reason);
    }

    private String extractUsername(String bearer) {
        if (bearer.startsWith("Bearer ")) {
            bearer = bearer.substring(7);
        }
        return jwtUtil.extractUsername(bearer);
    }
}
