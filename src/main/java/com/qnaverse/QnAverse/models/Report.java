package com.qnaverse.QnAverse.models;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a report entity for reporting inappropriate content.
 * 
 * Database Columns:
 *  - id: primary key
 *  - user_id: the reporting user's id (required)
 *  - question_id: (nullable) if content is a question
 *  - answer_id: (nullable) if content is an answer
 *  - reason: report reason
 *  - created_at: timestamp
 *  - content_id: id of the reported content (question/answer)
 *  - content_type: "QUESTION" or "ANSWER"
 *  - reported_by: id of the reporting user
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The reporting user mapped to user_id column.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The reporting user mapped to reported_by column.
    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    // ID of the reported question or answer
    @Column(nullable = false)
    private Long contentId;

    // "QUESTION" or "ANSWER"
    @Column(nullable = false)
    private String contentType;

    // Report reason
    @Column(nullable = false)
    private String reason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    /**
     * Constructs a new Report.
     * Both user and reportedBy are set to the reporting user.
     *
     * @param reportedBy the reporting user
     * @param contentId the id of the reported content
     * @param contentType the type of content ("QUESTION" or "ANSWER")
     * @param reason the reason for reporting
     */
    public Report(User reportedBy, Long contentId, String contentType, String reason) {
        this.reportedBy = reportedBy;
        this.user = reportedBy; // Set the user field so that user_id is populated.
        this.contentId = contentId;
        this.contentType = contentType;
        this.reason = reason;
    }
}
