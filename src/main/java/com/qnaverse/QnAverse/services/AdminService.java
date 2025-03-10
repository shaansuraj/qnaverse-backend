package com.qnaverse.QnAverse.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.models.Answer;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.Report;
import com.qnaverse.QnAverse.repositories.AnswerRepository;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.ReportRepository;

@Service
public class AdminService {

    private final ReportRepository reportRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public AdminService(ReportRepository reportRepository,
                        QuestionRepository questionRepository,
                        AnswerRepository answerRepository) {
        this.reportRepository = reportRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    /**
     * Fetches all reported content (questions or answers).
     */
    public ResponseEntity<List<Report>> getReportedContent(String contentType) {
        return ResponseEntity.ok(reportRepository.findByContentType(contentType));
    }

    /**
     * Deletes reported content (Admin Only) and removes associated report records.
     */
    public ResponseEntity<?> deleteReportedContent(Long contentId, String contentType) {
        if (contentType.equalsIgnoreCase("QUESTION")) {
            Optional<Question> questionOptional = questionRepository.findById(contentId);
            if (questionOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Question not found");
            }
            // Delete the question content
            questionRepository.delete(questionOptional.get());
            // Delete all reports associated with this question
            List<Report> reports = reportRepository.findByContentTypeAndContentId("QUESTION", contentId);
            reportRepository.deleteAll(reports);
            return ResponseEntity.ok("Question deleted successfully and related reports removed.");
        } else if (contentType.equalsIgnoreCase("ANSWER")) {
            Optional<Answer> answerOptional = answerRepository.findById(contentId);
            if (answerOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Answer not found");
            }
            // Delete the answer content
            answerRepository.delete(answerOptional.get());
            // Delete all reports associated with this answer
            List<Report> reports = reportRepository.findByContentTypeAndContentId("ANSWER", contentId);
            reportRepository.deleteAll(reports);
            return ResponseEntity.ok("Answer deleted successfully and related reports removed.");
        }
        return ResponseEntity.badRequest().body("Invalid content type.");
    }
    /**
     * Retrieves all unapproved questions for admin review.
     */
    public ResponseEntity<List<Question>> getUnapprovedQuestions() {
        List<Question> unapproved = questionRepository.findAll().stream()
                .filter(q -> !q.isApproved())
                .toList();
        return ResponseEntity.ok(unapproved);
    }

    public ResponseEntity<?> ignoreReportedContent(Long contentId, String contentType) {
        // Fetch all report records for the given content ID and type
        List<Report> reports = reportRepository.findAll().stream()
                .filter(r -> r.getContentId().equals(contentId)
                        && r.getContentType().equalsIgnoreCase(contentType))
                .collect(Collectors.toList());
        if (reports.isEmpty()) {
            return ResponseEntity.badRequest().body("No reports found for the specified content.");
        }
        reportRepository.deleteAll(reports);
        return ResponseEntity.ok("Report ignored successfully. Report records removed.");
    }
}
