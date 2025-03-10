package com.qnaverse.QnAverse.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qnaverse.QnAverse.dto.MetricsDTO;
import com.qnaverse.QnAverse.models.Report;
import com.qnaverse.QnAverse.services.AdminService;
import com.qnaverse.QnAverse.services.MetricsService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
        private final MetricsService metricsService;


    public AdminController(AdminService adminService, MetricsService metricsService) {
        this.adminService = adminService;
        this.metricsService = metricsService;
    }

    /**
     * Fetches reported content (Admin Only).
     */
    @GetMapping("/reports/{contentType}")
    // /api/admin/reports/ANSWER {AWER_ID, CONTENT, USER_ID, .....}
    public ResponseEntity<List<Report>> getReportedContent(@PathVariable String contentType) {
        return adminService.getReportedContent(contentType);
    }

    /**
     * Deletes a reported question or answer (Admin Only).
     */
    @DeleteMapping("/delete/{contentId}/{contentType}")
    public ResponseEntity<?> deleteReportedContent(@PathVariable Long contentId, @PathVariable String contentType) {
        return adminService.deleteReportedContent(contentId, contentType);
    }

    @DeleteMapping("/ignore/{contentId}/{contentType}")
    public ResponseEntity<?> ignoreReportedContent(@PathVariable Long contentId, @PathVariable String contentType) {
        return adminService.ignoreReportedContent(contentId, contentType);
    }
    
    /**
     * Retrieves unapproved questions for admin review.
     */
    @GetMapping("/questions/unapproved")
    public ResponseEntity<?> getUnapprovedQuestions() {
        return adminService.getUnapprovedQuestions();
    }

    /**
     * GET /api/admin/metrics
     * Returns various user/question metrics in JSON.
     */
    @GetMapping("/metrics")
    public ResponseEntity<MetricsDTO> getMetrics() {
        MetricsDTO dto = metricsService.getAllMetrics();
        return ResponseEntity.ok(dto);
    }
}

