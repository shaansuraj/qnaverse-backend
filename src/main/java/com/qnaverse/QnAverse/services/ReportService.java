// package com.qnaverse.QnAverse.services;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// import com.qnaverse.QnAverse.dto.ReportDTO;
// import com.qnaverse.QnAverse.models.Answer;
// import com.qnaverse.QnAverse.models.Question;
// import com.qnaverse.QnAverse.models.Report;
// import com.qnaverse.QnAverse.models.User;
// import com.qnaverse.QnAverse.repositories.AnswerRepository;
// import com.qnaverse.QnAverse.repositories.QuestionRepository;
// import com.qnaverse.QnAverse.repositories.ReportRepository;
// import com.qnaverse.QnAverse.repositories.UserRepository;

// @Service
// public class ReportService {

//     private final ReportRepository reportRepository;
//     private final UserRepository userRepository;
//     private final QuestionRepository questionRepository;
//     private final AnswerRepository answerRepository;

//     public ReportService(ReportRepository reportRepository,
//                          UserRepository userRepository,
//                          QuestionRepository questionRepository,
//                          AnswerRepository answerRepository) {
//         this.reportRepository = reportRepository;
//         this.userRepository = userRepository;
//         this.questionRepository = questionRepository;
//         this.answerRepository = answerRepository;
//     }

//     /**
//      * Create a new report. If total reports >= 10, auto-hide content.
//      */
//     public ResponseEntity<?> reportContent(String username, Long contentId, String contentType, String reason) {
//         Optional<User> userOpt = userRepository.findByUsername(username);
//         if (userOpt.isEmpty()) {
//             return ResponseEntity.badRequest().body("Reporting user not found");
//         }
//         User reportedBy = userOpt.get();

//         if (!contentType.equalsIgnoreCase("QUESTION") && !contentType.equalsIgnoreCase("ANSWER")) {
//             return ResponseEntity.badRequest().body("Invalid content type");
//         }

//         // Save report
//         Report report = new Report(reportedBy, contentId, contentType.toUpperCase(), reason);
//         reportRepository.save(report);

//         // Check total reports for the content
//         long totalReports = reportRepository.countByContentIdAndContentType(contentId, contentType.toUpperCase());
//         if (totalReports >= 10) {
//             // Auto-hide content if threshold met
//             if (contentType.equalsIgnoreCase("QUESTION")) {
//                 Optional<Question> qOpt = questionRepository.findById(contentId);
//                 if (qOpt.isPresent()) {
//                     Question q = qOpt.get();
//                     q.setApproved(false); // Hides from feed
//                     questionRepository.save(q);
//                 }
//             } else if (contentType.equalsIgnoreCase("ANSWER")) {
//                 Optional<Answer> aOpt = answerRepository.findById(contentId);
//                 if (aOpt.isPresent()) {
//                     Answer a = aOpt.get();
//                     a.setHidden(true);
//                     answerRepository.save(a);
//                 }
//             }
//         }
//         return ResponseEntity.ok("Reported successfully. Current total reports = " + totalReports);
//     }

package com.qnaverse.QnAverse.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.dto.ReportDTO;
import com.qnaverse.QnAverse.models.Answer;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.Report;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.AnswerRepository;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.ReportRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ModeratorService moderatorService;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository,
                         ModeratorService moderatorService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.moderatorService = moderatorService;
    }

    /**
     * Creates a new report. For reported content, it calls the moderation service:
     * - For questions: Moderates text.
     * - For answers: Moderates text.
     * If content is safe, returns "The content is not sensitive".
     * If content is flagged, auto-hides it.
     */
    // public ResponseEntity<?> reportContent(String username, Long contentId, String contentType, String reason) {
    //     Optional<User> userOpt = userRepository.findByUsername(username);
    //     if (userOpt.isEmpty()) {
    //         return ResponseEntity.badRequest().body("Reporting user not found");
    //     }
    //     User reportedBy = userOpt.get();

    //     if (!contentType.equalsIgnoreCase("QUESTION") && !contentType.equalsIgnoreCase("ANSWER")) {
    //         return ResponseEntity.badRequest().body("Invalid content type");
    //     }

    //     Report report = new Report(reportedBy, contentId, contentType.toUpperCase(), reason);
    //     reportRepository.save(report);

    //     boolean contentSafe = true;
    //     if (contentType.equalsIgnoreCase("QUESTION")) {
    //         Optional<Question> qOpt = questionRepository.findById(contentId);
    //         if (qOpt.isPresent()) {
    //             Question q = qOpt.get();
    //             try {
    //                 Map<String, Object> textModeration = moderatorService.moderateText(q.getContent());
    //                 boolean textSafe = (Boolean) textModeration.get("safe");
    //                 contentSafe = textSafe;
    //                 if (!contentSafe) {
    //                     q.setApproved(false);
    //                     questionRepository.save(q);
    //                 }
    //             } catch (Exception e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     } else if (contentType.equalsIgnoreCase("ANSWER")) {
    //         Optional<Answer> aOpt = answerRepository.findById(contentId);
    //         if (aOpt.isPresent()) {
    //             Answer a = aOpt.get();
    //             try {
    //                 Map<String, Object> textModeration = moderatorService.moderateText(a.getContent());
    //                 contentSafe = (Boolean) textModeration.get("safe");
    //                 if (!contentSafe) {
    //                     a.setHidden(true);
    //                     answerRepository.save(a);
    //                 }
    //             } catch (Exception e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     }

    //     if (contentSafe) {
    //         return ResponseEntity.ok("The content is not sensitive.");
    //     } else {
    //         return ResponseEntity.ok("Reported successfully. Content is sensitive and has been auto-hidden.");
    //     }
    // }

    public ResponseEntity<?> reportContent(String username, Long contentId, String contentType, String reason) {
        // Find the reporting user by username
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reporting user not found");
        }
        User reportedBy = userOpt.get();
    
        // Validate content type: must be either QUESTION or ANSWER
        if (!contentType.equalsIgnoreCase("QUESTION") && !contentType.equalsIgnoreCase("ANSWER")) {
            return ResponseEntity.badRequest().body("Invalid content type");
        }
    
        // Create a new Report and save it
        Report report = new Report(reportedBy, contentId, contentType.toUpperCase(), reason);
        reportRepository.save(report);
    
        // Count total reports for the given content
        long totalReports = reportRepository.countByContentIdAndContentType(contentId, contentType.toUpperCase());
    
        // Return a success response with the current total reports
        return ResponseEntity.ok("Reported successfully. Current total reports = " + totalReports);
    }
    

    /**
     * Retrieves enriched report details for the given content type.
     * Maps each Report to a ReportDTO containing additional details.
     */
    public List<ReportDTO> getDetailedReports(String contentType) {
        List<Report> reports = reportRepository.findByContentType(contentType.toUpperCase());
        List<ReportDTO> dtos = new ArrayList<>();
        for (Report report : reports) {
            ReportDTO dto = new ReportDTO();
            dto.setId(report.getId());
            dto.setContentId(report.getContentId());
            dto.setContentType(report.getContentType());
            dto.setReason(report.getReason());
            dto.setCreatedAt(report.getCreatedAt());
            dto.setReportedByUsername(report.getReportedBy().getUsername());
            
            if (contentType.equalsIgnoreCase("QUESTION")) {
                Optional<Question> qOpt = questionRepository.findById(report.getContentId());
                if (qOpt.isPresent()) {
                    Question q = qOpt.get();
                    dto.setReportedContent(q.getContent());
                    dto.setReportedMediaUrl(q.getMediaUrl());
                    dto.setContentOwnerUsername(q.getUser().getUsername());
                }
            } else if (contentType.equalsIgnoreCase("ANSWER")) {
                Optional<Answer> aOpt = answerRepository.findById(report.getContentId());
                if (aOpt.isPresent()) {
                    Answer a = aOpt.get();
                    dto.setReportedContent(a.getContent());
                    dto.setReportedMediaUrl(null); // Answers don't have media
                    dto.setContentOwnerUsername(a.getUser().getUsername());
                }
            }
            dtos.add(dto);
        }
        return dtos;
    }
}
