package com.qnaverse.QnAverse.services;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.qnaverse.QnAverse.models.*;
import com.qnaverse.QnAverse.repositories.AnswerRepository;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.ReportRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

/**
 * Service for handling content reports (question/answer).
 * Auto-hides content if 10+ reports.
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    /**
     * Create a new report. If total reports >= 10, auto-hide.
     */
    public ResponseEntity<?> reportContent(String username, Long contentId, String contentType, String reason) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reporting user not found");
        }
        User reportedBy = userOpt.get();

        if (!contentType.equalsIgnoreCase("QUESTION") && !contentType.equalsIgnoreCase("ANSWER")) {
            return ResponseEntity.badRequest().body("Invalid content type");
        }

        // Save report
        Report report = new Report(reportedBy, contentId, contentType.toUpperCase(), reason);
        reportRepository.save(report);

        // Check how many times it has been reported
        long totalReports = reportRepository.countByContentIdAndContentType(contentId, contentType.toUpperCase());
        if (totalReports >= 10) {
            // Auto-hide
            if (contentType.equalsIgnoreCase("QUESTION")) {
                Optional<Question> qOpt = questionRepository.findById(contentId);
                if (qOpt.isPresent()) {
                    Question q = qOpt.get();
                    // Mark as not approved to hide from feed
                    q.setApproved(false);
                    questionRepository.save(q);
                }
            } else {
                Optional<Answer> aOpt = answerRepository.findById(contentId);
                if (aOpt.isPresent()) {
                    Answer a = aOpt.get();
                    a.setHidden(true);
                    answerRepository.save(a);
                }
            }
        }

        return ResponseEntity.ok("Reported successfully. Current total reports = " + totalReports);
    }
}
