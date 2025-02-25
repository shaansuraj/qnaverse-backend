package com.qnaverse.QnAverse.services;

import java.util.List;
import java.util.Optional;

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
     * Deletes reported content (Admin Only).
     */
    public ResponseEntity<?> deleteReportedContent(Long contentId, String contentType) {
        if (contentType.equalsIgnoreCase("QUESTION")) {
            Optional<Question> questionOptional = questionRepository.findById(contentId);
            if (questionOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Question not found");
            }
            questionRepository.delete(questionOptional.get());
            return ResponseEntity.ok("Question deleted successfully.");
        } else if (contentType.equalsIgnoreCase("ANSWER")) {
            Optional<Answer> answerOptional = answerRepository.findById(contentId);
            if (answerOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Answer not found");
            }
            answerRepository.delete(answerOptional.get());
            return ResponseEntity.ok("Answer deleted successfully.");
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
}
