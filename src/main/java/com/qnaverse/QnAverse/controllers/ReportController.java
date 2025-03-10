package com.qnaverse.QnAverse.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qnaverse.QnAverse.dto.ReportDTO;
import com.qnaverse.QnAverse.services.ReportService;
import com.qnaverse.QnAverse.utils.JwtUtil;

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
     * Example: POST /api/report/18/QUESTION?reason=bad+post
     */
    @PostMapping("/{contentId}/{contentType}")
    public ResponseEntity<?> reportContent(@RequestHeader("Authorization") String token,
                                           @PathVariable Long contentId,
                                           @PathVariable String contentType,
                                           @RequestParam String reason) {
        String username = extractUsername(token);
        return reportService.reportContent(username, contentId, contentType, reason);
    }

    /**
     * Retrieves enriched report details for a given content type.
     * Example: GET /api/report/detailed/QUESTION
     */
    @GetMapping("/detailed/{contentType}")
    public ResponseEntity<List<ReportDTO>> getDetailedReports(@PathVariable String contentType) {
        List<ReportDTO> dtos = reportService.getDetailedReports(contentType);
        return ResponseEntity.ok(dtos);
    }

    private String extractUsername(String bearer) {
        if (bearer.startsWith("Bearer ")) {
            bearer = bearer.substring(7);
        }
        return jwtUtil.extractUsername(bearer);
    }
}
