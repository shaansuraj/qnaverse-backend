package com.qnaverse.QnAverse.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for reported content.
 * This DTO aggregates:
 *  - Report details (id, contentId, contentType, reason, createdAt)
 *  - The username of the reporting user (reportedByUsername)
 *  - The reported content details:
 *      - reportedContent: The text content of the question/answer.
 *      - reportedMediaUrl: The media URL if the reported content is a question with media.
 *      - contentOwnerUsername: The username of the user who originally posted the content.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReportDTO {
    private Long id;
    private Long contentId;
    private String contentType;
    private String reason;
    private Date createdAt;
    private String reportedByUsername;
    
    // Additional fields for a richer report view:
    private String reportedContent;
    private String reportedMediaUrl;
    private String contentOwnerUsername;
}
