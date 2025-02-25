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

    // The user who reported
    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    // ID of the reported question or answer
    @Column(nullable = false)
    private Long contentId;

    // "QUESTION" or "ANSWER"
    @Column(nullable = false)
    private String contentType;

    // reason
    @Column(nullable = false)
    private String reason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Report(User reportedBy, Long contentId, String contentType, String reason) {
        this.reportedBy = reportedBy;
        this.contentId = contentId;
        this.contentType = contentType;
        this.reason = reason;
    }
}
